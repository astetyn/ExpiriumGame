package com.astetyne.expirium.server.backend;

import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.entity.ExpiEntity;
import com.astetyne.expirium.server.api.entity.ExpiPlayer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ServerGateway extends TerminableLooper {

    private ServerSocket server;
    private final int port;
    private final List<ServerPlayerGateway> joiningClients;
    private final List<ServerPlayerGateway> leavingClients;

    public ServerGateway(int port) {
        this.port = port;
        joiningClients = new ArrayList<>();
        leavingClients = new ArrayList<>();
    }

    @Override
    public void run() {

        try {

            server = new ServerSocket(port);

            while(isRunning()) {

                System.out.println("Listening for incoming clients...");
                Socket client = server.accept();
                Thread t = new Thread(new ServerPlayerGateway(client));
                t.setName("Server (client) gateway");
                t.start();

            }

        }catch(IOException e) {
            end();
        }
        System.out.println("Server GW closed.");
    }

    @Override
    public void end() {
        super.end();
        try {
            if(server != null) server.close();
        }catch(IOException ignored) {}
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

    public void resolveJoiningAndLeavingPlayers() {

        synchronized(leavingClients) {
            for(ServerPlayerGateway gateway : leavingClients) {
                Iterator<ExpiPlayer> it = GameServer.get().getPlayers().iterator();
                while(it.hasNext()) {
                    ExpiPlayer p = it.next();
                    if(gateway == p.getGateway()) {
                        it.remove();
                        for(ExpiPlayer pp : GameServer.get().getPlayers()) {
                            pp.getNetManager().putEntityDespawnPacket(p);
                        }
                        p.destroySafe();
                        p.getGateway().end();
                        System.out.println("Player "+p.getName()+" left the server.");
                        break;
                    }
                }
            }
            leavingClients.clear();
        }

        List<ExpiPlayer> joiningPlayers = new ArrayList<>();

        synchronized(joiningClients) {
            // read init data from client
            for(ServerPlayerGateway gateway : joiningClients) {
                PacketInputStream in = gateway.getIn();
                in.swap();
                int packetID = in.getInt();
                if(packetID != 10) {
                    System.out.println("Wrong join packet id, refusing.");
                    continue;
                }
                String name = in.getString();
                ExpiPlayer ep = new ExpiPlayer(GameServer.get().getWorld().getSaveLocationForSpawn(), gateway, name);
                joiningPlayers.add(ep);
                GameServer.get().getWorldLoaders().add(new WorldLoader(ep));
            }
            joiningClients.clear();
        }

        for(ExpiPlayer newPlayer : joiningPlayers) {

            // create list of entities (for new players)
            List<ExpiEntity> alreadyExistingEntities = new ArrayList<>();
            for(ExpiEntity e : GameServer.get().getEntities()) {
                if(e == newPlayer) continue;
                alreadyExistingEntities.add(e);
            }

            // initial packet for new player
            newPlayer.getNetManager().putInitDataPacket(GameServer.get().getWorld().getTerrain(), alreadyExistingEntities);
            newPlayer.getGateway().getOut().swap();

            synchronized(newPlayer.getGateway().getJoinLock()) {
                newPlayer.getGateway().getJoinLock().notify();
            }

            // notify all players about new players
            for(ExpiPlayer p : GameServer.get().getPlayers()) {
                if(p == newPlayer) continue;
                p.getNetManager().putEntitySpawnPacket(newPlayer);
            }
        }
        joiningPlayers.clear();
    }
}
