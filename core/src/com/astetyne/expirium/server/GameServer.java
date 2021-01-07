package com.astetyne.expirium.server;

import com.astetyne.expirium.server.api.entities.ExpiDroppedItem;
import com.astetyne.expirium.server.api.entities.ExpiEntity;
import com.astetyne.expirium.server.api.entities.ExpiPlayer;
import com.astetyne.expirium.server.api.world.ExpiWorld;
import com.astetyne.expirium.server.backend.ServerGateway;
import com.astetyne.expirium.server.backend.ServerPlayerGateway;
import com.astetyne.expirium.server.backend.TickLooper;
import com.astetyne.expirium.server.backend.WorldLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameServer implements Runnable {

    private static GameServer server;

    private final ExpiWorld expiWorld;
    private final ServerGateway serverGateway;
    private final TickLooper tickLooper;

    private final List<ServerPlayerGateway> joiningClients;
    private final List<ServerPlayerGateway> leavingClients;
    private final List<WorldLoader> worldLoaders;
    private final List<ExpiEntity> entities;
    private final List<ExpiPlayer> players;
    private final List<ExpiDroppedItem> droppedItems;

    // you MUST create this object on dedicated thread, it will create endless loop
    public GameServer() {

        server = this;

        System.out.println("Booting server...");

        joiningClients = new ArrayList<>();
        leavingClients = new ArrayList<>();
        worldLoaders = new ArrayList<>();
        entities = new ArrayList<>();
        players = new ArrayList<>();
        droppedItems = new ArrayList<>();

        expiWorld = new ExpiWorld("svet");

        tickLooper = new TickLooper();
        serverGateway = new ServerGateway();

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
        for(ExpiPlayer p : players) {
            p.getGateway().end();
        }
    }

    public void onTick() {

        expiWorld.onTick();

        for(ExpiPlayer pp : players) {
            pp.getGateway().getManager().putEnviroPacket();
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

    public void playerPreJoinAsync(ServerPlayerGateway p) {
        synchronized(joiningClients) {
            joiningClients.add(p);
        }
    }

    public void playerPreLeaveAsync(ServerPlayerGateway p) {
        synchronized(leavingClients) {
            leavingClients.add(p);
        }
    }

    public ExpiWorld getWorld() {
        return expiWorld;
    }

    public TickLooper getTickLooper() {
        return tickLooper;
    }

    public static GameServer get() {
        return server;
    }

    public ServerGateway getServerGateway() {
        return serverGateway;
    }

    public List<ServerPlayerGateway> getJoiningClients() {
        return joiningClients;
    }

    public List<ServerPlayerGateway> getLeavingClients() {
        return leavingClients;
    }

    public List<ExpiDroppedItem> getDroppedItems() {
        return droppedItems;
    }

    public List<WorldLoader> getWorldLoaders() {
        return worldLoaders;
    }
}
