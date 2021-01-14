package com.astetyne.expirium.server;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.server.api.entity.ExpiEntity;
import com.astetyne.expirium.server.api.entity.ExpiPlayer;
import com.astetyne.expirium.server.api.event.EventManager;
import com.astetyne.expirium.server.api.world.ExpiWorld;
import com.astetyne.expirium.server.api.world.WorldFileManager;
import com.astetyne.expirium.server.api.world.generator.CreateWorldPreferences;
import com.astetyne.expirium.server.api.world.generator.LoadWorldPreferences;
import com.astetyne.expirium.server.api.world.generator.WorldLoadingException;
import com.astetyne.expirium.server.backend.TickLooper;
import com.astetyne.expirium.server.backend.WorldLoader;
import com.astetyne.expirium.server.net.ServerGateway;
import com.badlogic.gdx.utils.Disposable;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class GameServer implements Runnable, Disposable {

    private final ExpiWorld expiWorld;
    private final ServerGateway serverGateway;
    private final TickLooper tickLooper;

    private final List<WorldLoader> worldLoaders;
    private final List<ExpiEntity> entities;
    private final List<ExpiPlayer> players;

    private final HashMap<Integer, ExpiEntity> entitiesID;

    private final EventManager eventManager;
    private final WorldFileManager fileManager;

    /** Object representing whole game server.
     * Note that you can create only one instance of this object and because server can be stopped in any
     * moment and garbage collected, there MUST NOT be any static references to this server or its sub-parts.
     *
     * You should create this object on dedicated thread, it will create endless loop.
     *
     * @param worldSettings Settings for loading or creating new world, for loading name is enough.
     * @param createNew If server should create new world.
     * @param tps Target TPS on which server will run.
     * @param port Server port for TCP socket.
     */
    public GameServer(ServerPreferences serverPreferences) throws WorldLoadingException {

        ExpiGame.get().server = this;

        System.out.println("Booting server...");

        worldLoaders = new ArrayList<>();
        entities = new ArrayList<>();
        players = new ArrayList<>();
        entitiesID = new HashMap<>();

        eventManager = new EventManager();

        fileManager = new WorldFileManager(serverPreferences.worldPreferences.worldName);

        if(serverPreferences.worldPreferences instanceof LoadWorldPreferences) {
            DataInputStream in = fileManager.getWorldDataStream();
            expiWorld = new ExpiWorld(in);
            expiWorld.loadWorldStuff(in);
        }else {
            expiWorld = new ExpiWorld((CreateWorldPreferences) serverPreferences.worldPreferences);
        }

        tickLooper = new TickLooper(serverPreferences.tps);
        serverGateway = new ServerGateway(serverPreferences.port);

    }

    @Override
    public void run() {
        Thread t = new Thread(serverGateway);
        t.setName("Server gateway");
        t.start();
        tickLooper.run();
    }

    public void stop() {
        tickLooper.end();
        serverGateway.end();
        for(ExpiPlayer p : players) {
            p.getGateway().end();
        }
        synchronized(GameServer.get().getTickLooper().getTickLock()) {
            GameServer.get().getTickLooper().getTickLock().notifyAll();
        }
        try {
            fileManager.saveWorld(expiWorld);
        }catch(IOException e) {
            e.printStackTrace();
        }
        dispose();
    }

    public void dispose() {
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
}
