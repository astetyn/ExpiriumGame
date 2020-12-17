package com.astetyne.main.net.server;

import com.astetyne.main.net.server.entities.ServerDroppedItem;
import com.astetyne.main.net.server.entities.ServerEntity;
import com.astetyne.main.net.server.entities.ServerPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameServer implements Runnable {

    private static GameServer server;

    private final ServerWorld serverWorld;
    private final ServerGateway serverGateway;
    private final TickLooper tickLooper;

    private final HashMap<Integer, ServerEntity> entitiesID;
    private final List<ServerPlayerGateway> joiningClients;
    private final List<ServerPlayerGateway> leavingClients;
    private final List<ServerPlayer> players;
    private final List<ServerDroppedItem> droppedItems;

    // you MUST create this object on dedicated thread, it will create endless loop
    public GameServer() {

        server = this;

        entitiesID = new HashMap<>();

        System.out.println("booting server...");

        joiningClients = new ArrayList<>();
        leavingClients = new ArrayList<>();
        players = new ArrayList<>();
        droppedItems = new ArrayList<>();

        serverWorld = new ServerWorld("svet");

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
        for(ServerPlayer p : players) {
            p.getGateway().end();
        }
    }

    public List<ServerPlayer> getPlayers() {
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

    public ServerWorld getServerWorld() {
        return serverWorld;
    }

    public TickLooper getTickLooper() {
        return tickLooper;
    }

    public static GameServer getServer() {
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

    public HashMap<Integer, ServerEntity> getEntitiesID() {
        return entitiesID;
    }

    public List<ServerDroppedItem> getDroppedItems() {
        return droppedItems;
    }
}
