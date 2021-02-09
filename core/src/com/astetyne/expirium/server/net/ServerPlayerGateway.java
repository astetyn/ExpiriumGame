package com.astetyne.expirium.server.net;

import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.TerminableLooper;
import com.astetyne.expirium.server.core.entity.ExpiPlayer;

import java.io.IOException;
import java.net.Socket;

public class ServerPlayerGateway extends TerminableLooper {

    private final Socket client;
    private final Object joinLock;
    private PacketInputStream in;
    private PacketOutputStream out;
    private ServerPacketManager packetManager;
    private int traffic;
    private long time;
    private ExpiPlayer owner;
    private final ExpiServer server;

    public ServerPlayerGateway(Socket client, ExpiServer server) {
        this.client = client;
        this.server = server;
        joinLock = new Object();
        traffic = 0;
        time = 0;
    }

    @Override
    public void run() {

        try {

            System.out.println("New client connected.");
            client.setTcpNoDelay(true);

            in = new PacketInputStream(client.getInputStream());
            out = new PacketOutputStream(client.getOutputStream());

            client.setSoTimeout(10000);
            int readBytes = in.fillBuffer();
            if(readBytes == -1) stop();

            in.swap();
            // verification if connecting client is game or not
            int packetID = in.getInt();
            if(packetID != 10) {
                System.out.println("Wrong join packet id, refusing.");
                return;
            }
            System.out.println("Correct join packet, moving to registration.");

            server.getServerGateway().playerPreJoinAsync(this);

            synchronized(joinLock) {
                joinLock.wait(); // wait until main looper recognize new player and populate init actions
            }

            in.reset();

        }catch(Exception e) {
            try {
                client.close();
            }catch(IOException ignored) {}
            System.out.println("Player failed during connecting to server.");
            return;
        }

        try {

            while(isRunning()) {
                out.flush();
                client.setSoTimeout(30000);
                int readBytes = in.fillBuffer();
                if(readBytes == -1) {
                    server.getServerGateway().playerPreLeaveAsync(owner);
                    return;
                }

                traffic += readBytes;
                if(time + 10000 < System.currentTimeMillis()) {
                    time = System.currentTimeMillis();
                    System.out.println("Server traffic (Bp10s): "+traffic+" ping (ms): "+in.getPing());
                    traffic = 0;
                }

                synchronized(server.getTickLooper().getTickLock()) {
                    server.getTickLooper().getTickLock().wait();
                    in.swap();
                    out.swap();
                }
            }

        }catch(IOException | InterruptedException e) {
            server.getServerGateway().playerPreLeaveAsync(owner);
        }
        System.out.println("Channel with client \""+owner.getName()+"\" closed.");
    }

    @Override
    public void stop() {
        super.stop();
        try {
            client.close();
        }catch(IOException ignored) {}
    }

    public void setOwner(ExpiPlayer owner) {
        this.owner = owner;
        packetManager = new ServerPacketManager(server, owner);
    }

    public ServerPacketManager getManager() {
        return packetManager;
    }

    public Object getJoinLock() {
        return joinLock;
    }

    public PacketInputStream getIn() {
        return in;
    }

    public PacketOutputStream getOut() {
        return out;
    }
}
