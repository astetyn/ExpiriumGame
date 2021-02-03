package com.astetyne.expirium.server;

import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.server.backend.TickLooper;
import com.astetyne.expirium.server.backend.WorldLoader;
import com.astetyne.expirium.server.core.Saveable;
import com.astetyne.expirium.server.core.entity.ExpiEntity;
import com.astetyne.expirium.server.core.entity.ExpiPlayer;
import com.astetyne.expirium.server.core.event.EventManager;
import com.astetyne.expirium.server.core.world.ExpiWorld;
import com.astetyne.expirium.server.core.world.WorldFileManager;
import com.astetyne.expirium.server.core.world.generator.CreateWorldPreferences;
import com.astetyne.expirium.server.core.world.generator.WorldLoadingException;
import com.astetyne.expirium.server.core.world.modules.CampfireListener;
import com.astetyne.expirium.server.core.world.modules.RaspberryListener;
import com.astetyne.expirium.server.net.MulticastSender;
import com.astetyne.expirium.server.net.ServerGateway;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ExpiServer implements Saveable {

    private static final int version = 1;

    private ExpiWorld expiWorld;
    private final ServerGateway serverGateway;
    private final TickLooper tickLooper;

    private final List<WorldLoader> worldLoaders;
    private final List<ExpiEntity> entities;
    private final List<ExpiPlayer> players;

    private final HashMap<Integer, ExpiEntity> entitiesID;

    private final EventManager eventManager;
    private final WorldFileManager fileManager;

    private final List<Saveable> saveableModules;

    private final ServerFailListener failListener;
    private final MulticastSender multicastSender;

    private boolean fullyRunning;
    private boolean closeRequest;

    /** Object representing whole game server.
     * Only constructor will run on main thread, whole server will then run on dedicated thread.
     */
    public ExpiServer(ServerPreferences serverPreferences, ServerFailListener listener) {

        this.failListener = listener;
        closeRequest = false;

        System.out.println("Booting server...");

        worldLoaders = new ArrayList<>();
        entities = new ArrayList<>();
        players = new ArrayList<>();
        entitiesID = new HashMap<>();

        eventManager = new EventManager();

        tickLooper = new TickLooper(serverPreferences.tps, this);
        serverGateway = new ServerGateway(serverPreferences.port, this);
        multicastSender = new MulticastSender();

        fileManager = new WorldFileManager(this, serverPreferences.worldPreferences.worldName);

        saveableModules = new ArrayList<>();

        if(serverPreferences.worldPreferences instanceof CreateWorldPreferences) {

            // this is executed when user is creating new world and no data is saved
            expiWorld = new ExpiWorld((CreateWorldPreferences) serverPreferences.worldPreferences, this);
            saveableModules.add(new RaspberryListener(this));
            saveableModules.add(new CampfireListener(this));
        }else {

            try {
                // this is executed when user wants to load his world from file
                DataInputStream in = fileManager.loadGameServer();

                int savedVersion = in.readInt();
                if(savedVersion != version) failListener.onServerFail("World has incompatible version." +
                        " ("+savedVersion+") Your is: "+" ("+version+")");

                expiWorld = new ExpiWorld(in, this);

                // this must be loaded here, because it requires fully loaded ExpiWorld
                int entitiesSize = in.readInt();
                for(int i = 0; i < entitiesSize; i++) {
                    EntityType.getType(in.readInt()).initEntity(this, in);
                }

                saveableModules.add(new RaspberryListener(this, in));
                saveableModules.add(new CampfireListener(this, in));

                in.close();
            } catch(IOException e) {
                failListener.onServerFail("IOException during loading server.");
                return;
            }catch(WorldLoadingException e) {
                failListener.onServerFail("World is corrupted.");
                return;
            }
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
        multicastSender.stop();
        tickLooper.stop();
        serverGateway.stop();
        for(ExpiPlayer p : players) {
            p.getGateway().stop();
        }
        synchronized(tickLooper.getTickLock()) {
            tickLooper.getTickLock().notifyAll();
        }
        fileManager.saveGameServer();
        dispose();
    }

    private void performClose() {
        System.out.println("performing normal close");
        multicastSender.stop();
        tickLooper.stop();
        serverGateway.stop();
        for(ExpiPlayer p : players) {
            p.getGateway().stop();
        }
        synchronized(tickLooper.getTickLock()) {
            tickLooper.getTickLock().notifyAll();
        }
        fileManager.saveGameServer();
        dispose();
    }

    /**
     * Call this only ONCE during object lifetime.
     */
    private void dispose() {
        System.out.println("Disposing server world.");
        expiWorld.dispose();
    }

    public void onTick() {

        if(!fullyRunning && serverGateway.isFullyRunning()) {
            fullyRunning = true;
        }

        if(closeRequest && fullyRunning) {
            performClose();
            return;
        }

        Iterator<WorldLoader> it = worldLoaders.iterator();
        while(it.hasNext()) {
            WorldLoader wl = it.next();
            wl.update();
            if(wl.isCompleted()) it.remove();
        }

    }

    public List<ExpiEntity> getEntities() {
        return entities;
    }

    public List<ExpiPlayer> getPlayers() {
        return players;
    }

    public ExpiWorld getWorld() {
        return expiWorld;
    }

    public TickLooper getTickLooper() {
        return tickLooper;
    }

    public ServerGateway getServerGateway() {
        return serverGateway;
    }

    public List<WorldLoader> getWorldLoaders() {
        return worldLoaders;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public HashMap<Integer, ExpiEntity> getEntitiesID() {
        return entitiesID;
    }

    public WorldFileManager getFileManager() {
        return fileManager;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {

        out.writeInt(version);

        expiWorld.writeData(out);

        // players are saved independently - during logout
        int entitiesSize = 0;
        for(ExpiEntity e : entities) {
            if(e instanceof ExpiPlayer) continue;
            entitiesSize++;
        }
        out.writeInt(entitiesSize);
        for(ExpiEntity e : entities) {
            if(e instanceof ExpiPlayer) continue;
            out.writeInt(e.getType().getID());
            e.writeData(out);
        }

        for(Saveable saveable : saveableModules) {
            saveable.writeData(out);
        }
    }

    public int getRandomEntityID() {
        int randomID;
        do {
            randomID = (int)(Math.random()*Integer.MAX_VALUE);
        } while(entitiesID.containsKey(randomID));
        return randomID;
    }
}
