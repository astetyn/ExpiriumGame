package com.astetyne.expirium.server;

import com.astetyne.expirium.server.api.entities.ExpiDroppedItem;
import com.astetyne.expirium.server.api.entities.ExpiEntity;
import com.astetyne.expirium.server.api.entities.ExpiPlayer;
import com.astetyne.expirium.server.api.world.ExpiWorld;
import com.astetyne.expirium.server.api.world.inventory.ExpiInventory;
import com.astetyne.expirium.server.backend.ServerGateway;
import com.astetyne.expirium.server.backend.ServerPlayerGateway;
import com.astetyne.expirium.server.backend.TickLooper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameServer implements Runnable {

    private static GameServer server;

    private final ExpiWorld expiWorld;
    private final ServerGateway serverGateway;
    private final TickLooper tickLooper;

    private final HashMap<Integer, ExpiEntity> entitiesID;
    private final HashMap<Integer, ExpiInventory> inventoriesID;
    private final List<ServerPlayerGateway> joiningClients;
    private final List<ServerPlayerGateway> leavingClients;
    private final List<ExpiEntity> entities;
    private final List<ExpiPlayer> players;
    private final List<ExpiDroppedItem> droppedItems;
    private int serverTime;

    // you MUST create this object on dedicated thread, it will create endless loop
    public GameServer() {

        server = this;

        serverTime = 0;

        entitiesID = new HashMap<>();
        inventoriesID = new HashMap<>();

        System.out.println("Booting server...");

        joiningClients = new ArrayList<>();
        leavingClients = new ArrayList<>();
        entities = new ArrayList<>();
        players = new ArrayList<>();
        droppedItems = new ArrayList<>();

        expiWorld = new ExpiWorld("svet");

        tickLooper = new TickLooper();
        serverGateway = new ServerGateway();

    }

    @Override
    public void run() {
        new Thread(serverGateway).start();
        tickLooper.run();
    }

    public void stop() {
        tickLooper.end();
        for(ExpiPlayer p : players) {
            p.getGateway().end();
        }
    }

    public void onTick() {

        serverTime++;

        expiWorld.onTick();

        for(ExpiPlayer pp : players) {
            pp.getGateway().getManager().putEnviroPacket();
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

    public HashMap<Integer, ExpiEntity> getEntitiesID() {
        return entitiesID;
    }

    public HashMap<Integer, ExpiInventory> getInventoriesID() {
        return inventoriesID;
    }

    public List<ExpiDroppedItem> getDroppedItems() {
        return droppedItems;
    }

    public int getServerTime() {
        return serverTime;
    }
}
