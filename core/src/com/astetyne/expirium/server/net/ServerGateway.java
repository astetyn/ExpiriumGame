package com.astetyne.expirium.server.net;

import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.backend.TerminableLooper;
import com.astetyne.expirium.server.core.entity.ExpiEntity;
import com.astetyne.expirium.server.core.entity.ExpiPlayer;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerGateway extends TerminableLooper {

    private ServerSocket serverSocket;
    private final int port;
    private final List<ServerPlayerGateway> joiningClients;
    private final List<ExpiPlayer> leavingPlayers;
    private boolean fullyRunning;
    private final ExpiServer server;

    public ServerGateway(int port, ExpiServer server) {
        this.port = port;
        this.server = server;
        fullyRunning = false;
        joiningClients = new ArrayList<>();
        leavingPlayers = new ArrayList<>();
    }

    @Override
    public void run() {

        try {

            serverSocket = new ServerSocket(port);

            fullyRunning = true;

            while(isRunning()) {

                System.out.println("Listening for incoming clients...");
                Socket client = serverSocket.accept();
                Thread t = new Thread(new ServerPlayerGateway(client, server));
                t.setName("Server (client) gateway");
                t.start();

            }

        }catch(IOException | IllegalArgumentException e) {
            if(isRunning()) {
                server.faultClose();
                System.out.println("Server gateway fatal fail.");
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
        try {
            if(serverSocket != null) serverSocket.close();
        }catch(IOException ignored) {}
    }

    public boolean isFullyRunning() {
        return fullyRunning;
    }

    public void playerPreJoinAsync(ServerPlayerGateway p) {
        synchronized(joiningClients) {
            joiningClients.add(p);
        }
    }

    public void playerPreLeaveAsync(ExpiPlayer p) {
        synchronized(leavingPlayers) {
            leavingPlayers.add(p);
        }
    }

    public void resolveJoiningAndLeavingPlayers() {

        synchronized(leavingPlayers) {
            for(ExpiPlayer p : leavingPlayers) {
                server.getPlayers().remove(p);
                for(ExpiPlayer pp : server.getPlayers()) {
                    pp.getNetManager().putEntityDespawnPacket(p);
                }
                p.destroySafe();
                p.getGateway().stop();
                try {
                    server.getFileManager().savePlayer(p);
                }catch(IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Player "+p.getName()+" left the server.");
                break;
            }
            leavingPlayers.clear();
        }

        List<ExpiPlayer> joiningPlayers = new ArrayList<>();

        synchronized(joiningClients) {
            // read init data from client
            for(ServerPlayerGateway gateway : joiningClients) {
                // here we assume that joining client is verified and thus following reads will not fail
                PacketInputStream in = gateway.getIn();
                String name = in.getString();
                DataInputStream dataIn = server.getFileManager().getPlayerDataStream(name);
                ExpiPlayer ep = null;
                if(dataIn == null) {
                    ep = new ExpiPlayer(server, server.getWorld().getSpawnLocation(), gateway, name);
                }else {
                    try {
                        ep = new ExpiPlayer(server, dataIn, gateway);
                    }catch(IOException e) {
                        e.printStackTrace();
                    }
                }
                joiningPlayers.add(ep);
            }
            joiningClients.clear();
        }

        for(ExpiPlayer newPlayer : joiningPlayers) {

            // create list of entities (for new players)
            List<ExpiEntity> alreadyExistingEntities = new ArrayList<>();
            for(ExpiEntity e : server.getEntities()) {
                if(e == newPlayer) continue;
                alreadyExistingEntities.add(e);
            }

            // initial packet for new player
            newPlayer.getNetManager().putInitDataPacket(server.getWorld().getTerrain(), alreadyExistingEntities);
            newPlayer.getGateway().getOut().swap();

            synchronized(newPlayer.getGateway().getJoinLock()) {
                newPlayer.getGateway().getJoinLock().notify();
            }

            // notify all players about new players
            for(ExpiPlayer p : server.getPlayers()) {
                if(p == newPlayer) continue;
                p.getNetManager().putEntitySpawnPacket(newPlayer);
            }
        }
        joiningPlayers.clear();
    }
}
