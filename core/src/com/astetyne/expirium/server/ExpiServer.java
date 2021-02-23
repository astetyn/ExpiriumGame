package com.astetyne.expirium.server;

import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.core.WorldSaveable;
import com.astetyne.expirium.server.core.entity.Entity;
import com.astetyne.expirium.server.core.entity.player.Player;
import com.astetyne.expirium.server.core.event.EventManager;
import com.astetyne.expirium.server.core.world.World;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.astetyne.expirium.server.core.world.file.WorldFileManager;
import com.astetyne.expirium.server.core.world.file.WorldQuickInfo;
import com.astetyne.expirium.server.net.MulticastSender;
import com.astetyne.expirium.server.net.ServerGateway;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExpiServer implements WorldSaveable {

    private World world;
    private final ServerGateway serverGateway;
    private final TickLooper tickLooper;

    private final List<Entity> entities;
    private final List<Player> players;

    private final EventManager eventManager;
    private final WorldFileManager fileManager;

    private final List<WorldSaveable> saveableModules;

    private final ServerFailListener failListener;
    private final MulticastSender multicastSender;

    private boolean fullyRunning;
    private boolean closeRequest;

    /** Object representing whole game server.
     * Only constructor will run on main thread, whole server will then run on dedicated thread.
     */
    public ExpiServer(ServerFailListener listener, boolean createNew) {

        this.failListener = listener;
        closeRequest = false;

        System.out.println("Booting server...");

        entities = new ArrayList<>();
        players = new ArrayList<>();

        eventManager = new EventManager();

        tickLooper = new TickLooper(this);
        serverGateway = new ServerGateway(Consts.SERVER_PORT, this);
        multicastSender = new MulticastSender();

        fileManager = new WorldFileManager(this, createNew);

        saveableModules = new ArrayList<>();

        try {

            WorldQuickInfo wqi = WorldFileManager.getQuickInfo();
            if(wqi == null) throw new IOException("Cant load quick info.");

            if(wqi.worldVersion != Consts.VERSION) failListener.onServerFail("World has incompatible version." +
                    " ("+wqi.worldVersion+") Your is: "+" ("+Consts.VERSION+")");

            DataInputStream in = fileManager.getWorldInputStream();
            long startT = System.nanoTime();
            world = new World(in, wqi.tick, this);
            System.out.println("Loading world time (ms): "+(System.nanoTime() - startT)/1000000);

            // this must be loaded here, because it requires fully loaded ExpiWorld
            int entitiesSize = in.readInt();
            for(int i = 0; i < entitiesSize; i++) {
                EntityType type = EntityType.getType(in.readInt());
                world.spawnEntity(type, in);
            }
            in.close();
            System.out.println("Loading total time (ms): "+(System.nanoTime() - startT)/1000000);

        } catch(IOException e) {
            failListener.onServerFail("IOException during loading server.");
            e.printStackTrace();
            return;
        }


        Thread t = new Thread(serverGateway);
        t.setName("Server gateway");
        t.start();

        Thread t2 = new Thread(multicastSender);
        t2.setName("Multicast sender loop");
        t2.start();

        Thread t3 = new Thread(tickLooper);
        t3.setName("Tick Looper");
        t3.start();

    }

    /**
     * This will close the server, save world and release all resources. Server is unusable after being closed. You must
     * create new instance in order to use server again. You can call this method from any thread.
     */
    public void close() {
        closeRequest = true;
    }

    /**
     * This is only for fault-case when server unexpectedly fail.
     */
    public void faultClose() {
        System.out.println("performing fault close");
        fileManager.saveServer();
        multicastSender.stop();
        tickLooper.stop();
        serverGateway.stop();
        for(Player p : players) {
            p.getGateway().stop();
        }
        synchronized(tickLooper.getTickLock()) {
            tickLooper.getTickLock().notifyAll();
        }
        dispose();
    }

    private void performClose() {
        System.out.println("performing normal close");
        fileManager.saveServer();
        multicastSender.stop();
        tickLooper.stop();
        serverGateway.stop();
        for(Player p : players) {
            p.getGateway().stop();
        }
        synchronized(tickLooper.getTickLock()) {
            tickLooper.getTickLock().notifyAll();
        }
        dispose();
    }

    /**
     * Call this only ONCE during object lifetime.
     */
    private void dispose() {
        System.out.println("Disposing server world.");
        world.dispose();
    }

    public void onTick() {

        if(!fullyRunning && serverGateway.isFullyRunning()) {
            fullyRunning = true;
        }

        if(closeRequest && fullyRunning) {
            performClose();
            return;
        }

    }

    public List<Entity> getEntities() {
        return entities;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public World getWorld() {
        return world;
    }

    public TickLooper getTickLooper() {
        return tickLooper;
    }

    public ServerGateway getServerGateway() {
        return serverGateway;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public WorldFileManager getFileManager() {
        return fileManager;
    }

    @Override
    public void writeData(WorldBuffer out) {

        world.writeData(out);

        // players are saved independently - during logout
        int entitiesSize = 0;
        for(Entity e : entities) {
            if(e instanceof Player) continue;
            entitiesSize++;
        }
        out.writeInt(entitiesSize);
        for(Entity e : entities) {
            if(e instanceof Player) continue;
            out.writeInt(e.getType().getID());
            e.writeData(out);
        }

        /*for(WorldSaveable worldSaveable : saveableModules) {
            worldSaveable.writeData(out);
        }*/
    }

    public short getRandomEntityID() {
        short randomID;
        outer:
        while(true) {
            Random rand = new Random();
            randomID = (short) rand.nextInt(1 << 16);
            for(Entity entities : entities) {
                if(randomID == entities.getId()) continue outer;
            }
            break;
        }
        return randomID;
    }
}
