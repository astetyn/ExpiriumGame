package com.astetyne.expirium.server;

import com.astetyne.expirium.main.ExpiGame;
import com.astetyne.expirium.server.api.entity.ExpiEntity;
import com.astetyne.expirium.server.api.entity.ExpiPlayer;
import com.astetyne.expirium.server.api.event.EventManager;
import com.astetyne.expirium.server.api.world.ExpiWorld;
import com.astetyne.expirium.server.api.world.WorldSettings;
import com.astetyne.expirium.server.backend.ServerGateway;
import com.astetyne.expirium.server.backend.TickLooper;
import com.astetyne.expirium.server.backend.WorldLoader;
import com.badlogic.gdx.utils.Disposable;

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

    // you MUST create this object on dedicated thread, it will create endless loop
    public GameServer(WorldSettings worldSettings, boolean createNew, int tps, int port) {

        ExpiGame.get().server = this;

        System.out.println("Booting server...");

        worldLoaders = new ArrayList<>();
        entities = new ArrayList<>();
        players = new ArrayList<>();
        entitiesID = new HashMap<>();

        eventManager = new EventManager();

        expiWorld = new ExpiWorld(worldSettings, createNew);

        tickLooper = new TickLooper(tps);
        serverGateway = new ServerGateway(port);

        System.out.println("CREATINGGG: "+this);

    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("REMOVINGGG: "+this);
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
}
