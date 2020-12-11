package com.astetyne.main.net.server;

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
    private final List<ServerPlayer> players;

    // you MUST create this object on dedicated thread, it will create endless loop
    public GameServer() {

        server = this;

        entitiesID = new HashMap<>();

        System.out.println("booting server...");

        joiningClients = new ArrayList<>();
        players = new ArrayList<>();

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

    public void playerLeave(ServerPlayerGateway p) {
        /*synchronized(connectedPlayersLock) {
            connectedPlayers.remove(p);
        }*/
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

    public HashMap<Integer, ServerEntity> getEntitiesID() {
        return entitiesID;
    }
}
