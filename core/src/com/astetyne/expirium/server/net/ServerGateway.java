package com.astetyne.expirium.server.net;

import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.TerminableLooper;
import com.astetyne.expirium.server.core.entity.player.Player;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerGateway extends TerminableLooper {

    private ServerSocket serverSocket;
    private final int port;
    private final List<ServerPlayerGateway> joiningClients;
    private final List<Player> leavingPlayers;
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

    public void playerPreLeaveAsync(Player p) {
        synchronized(leavingPlayers) {
            leavingPlayers.add(p);
        }
    }

    public void resolveJoiningAndLeavingPlayers() {

        synchronized(leavingPlayers) {
            for(Player p : leavingPlayers) {
                server.getPlayers().remove(p);
                p.destroySafe();
                p.getGateway().stop();
                server.getFileManager().savePlayer(p);
                System.out.println("Player "+p.getName()+" left the server.");
                break;
            }
            leavingPlayers.clear();
        }

        synchronized(joiningClients) {

            for(ServerPlayerGateway gateway : joiningClients) {
                // here we assume that joining client is verified and thus following reads will not fail
                PacketInputStream in = gateway.getIn();
                String name = in.getString();
                Player p = server.getFileManager().loadPlayer(gateway, name);

                // initial packet
                p.getNetManager().putInitDataPacket(server.getEntities());
                p.getGateway().getOut().swap();
                synchronized(p.getGateway().getJoinLock()) {
                    p.getGateway().getJoinLock().notify();
                }
                System.out.println("Player "+p.getName()+" joined the server.");
            }
            joiningClients.clear();
        }
    }
}
