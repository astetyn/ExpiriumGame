package com.astetyne.expirium.server;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.server.api.Saveable;
import com.astetyne.expirium.server.api.entity.ExpiEntity;
import com.astetyne.expirium.server.api.entity.ExpiPlayer;
import com.astetyne.expirium.server.api.event.EventManager;
import com.astetyne.expirium.server.api.world.ExpiWorld;
import com.astetyne.expirium.server.api.world.WorldFileManager;
import com.astetyne.expirium.server.api.world.generator.CreateWorldPreferences;
import com.astetyne.expirium.server.api.world.generator.WorldLoadingException;
import com.astetyne.expirium.server.api.world.listeners.CampfireListener;
import com.astetyne.expirium.server.api.world.listeners.RaspberryListener;
import com.astetyne.expirium.server.backend.TickLooper;
import com.astetyne.expirium.server.backend.WorldLoader;
import com.astetyne.expirium.server.net.MulticastSender;
import com.astetyne.expirium.server.net.ServerGateway;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class GameServer implements Saveable {

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

    /** Object representing whole game server.
     * Note that you can create only one instance of this object and because server can be stopped in any
     * moment and garbage collected, there MUST NOT be any static references to this server or its sub-parts.
     *
     * You should create this object on dedicated thread, it will create endless loop.
     */
    public GameServer(ServerPreferences serverPreferences, ServerFailListener listener) {

        ExpiGame.get().server = this;

        this.failListener = listener;

        System.out.println("Booting server...");

        worldLoaders = new ArrayList<>();
        entities = new ArrayList<>();
        players = new ArrayList<>();
        entitiesID = new HashMap<>();

        eventManager = new EventManager();

        tickLooper = new TickLooper(serverPreferences.tps);
        serverGateway = new ServerGateway(serverPreferences.port);
        multicastSender = new MulticastSender();

        fileManager = new WorldFileManager(serverPreferences.worldPreferences.worldName);

        saveableModules = new ArrayList<>();

        if(serverPreferences.worldPreferences instanceof CreateWorldPreferences) {

            // this is executed when user is creating new world and no data is saved
            expiWorld = new ExpiWorld((CreateWorldPreferences) serverPreferences.worldPreferences);
            saveableModules.add(new RaspberryListener(this));
            saveableModules.add(new CampfireListener());
        }else {

            try {
                // this is executed when user wants to load his world from file
                DataInputStream in = fileManager.loadGameServer();

                int savedVersion = in.readInt();
                if(savedVersion != version) failListener.onServerFail("World has incompatible version." +
                        " ("+savedVersion+") Your is: "+" ("+version+")");

                expiWorld = new ExpiWorld(in);

                // this must be loaded here, because it requires fully loaded ExpiWorld
                int entitiesSize = in.readInt();
                for(int i = 0; i < entitiesSize; i++) {
                    EntityType.getType(in.readInt()).initEntity(in);
                }

                saveableModules.add(new RaspberryListener(in, this));
                saveableModules.add(new CampfireListener(in));

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

        tickLooper.run();
        // do not add any code here ticklooper is infinite loop
    }

    /**
     * This will close the server, save world and release all resources. Server is unusable after being closed. You must
     * create new instance in order to use server again. You can call this method from any thread.
     */
    public void close() {
        multicastSender.end();
        tickLooper.end();
        serverGateway.end();
        for(ExpiPlayer p : players) {
            p.getGateway().end();
        }
        synchronized(GameServer.get().getTickLooper().getTickLock()) {
            GameServer.get().getTickLooper().getTickLock().notifyAll();
        }
        try {
            fileManager.saveGameServer(this);
        }catch(IOException e) {
            e.printStackTrace();
        }
        dispose();
    }

    private void dispose() {
        expiWorld.dispose();
    }

    public void onTick() {

        expiWorld.onTick();

        for(ExpiPlayer pp : players) {
            pp.getNetManager().putEnviroPacket();
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

    public static GameServer get() {
        return ExpiGame.get().server;
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

        int entitiesSize = 0;
        for(ExpiEntity e : GameServer.get().getEntities()) {
            if(e instanceof ExpiPlayer) continue;
            entitiesSize++;
        }
        out.writeInt(entitiesSize);
        for(ExpiEntity e : GameServer.get().getEntities()) {
            if(e instanceof ExpiPlayer) continue;
            out.writeInt(e.getType().getID());
            e.writeData(out);
        }

        for(Saveable saveable : saveableModules) {
            saveable.writeData(out);
        }
    }
}
