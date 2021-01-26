package com.astetyne.expirium.server.net;

import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.api.entity.ExpiPlayer;
import com.astetyne.expirium.server.backend.TerminableLooper;

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

    public ServerPlayerGateway(Socket client) {
        this.client = client;
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

            client.setSoTimeout(5000);
            int readBytes = in.fillBuffer();
            if(readBytes == -1) stop();

            ExpiServer.get().getServerGateway().playerPreJoinAsync(this);

            synchronized(joinLock) {
                joinLock.wait(); // wait until main looper recognize new player and populate init actions
            }

            in.reset();

            while(isRunning()) {
                out.flush();
                client.setSoTimeout(5000);
                readBytes = in.fillBuffer();
                if(readBytes == -1) {
                    ExpiServer.get().getServerGateway().playerPreLeaveAsync(owner);
                    return;
                }

                traffic += readBytes;
                if(time + 5000 < System.currentTimeMillis()) {
                    time = System.currentTimeMillis();
                    System.out.println("Server traffic (bytes): "+traffic+" ping (ms): "+in.getPing());
                    traffic = 0;
                }

                synchronized(ExpiServer.get().getTickLooper().getTickLock()) {
                    ExpiServer.get().getTickLooper().getTickLock().wait();
                    in.swap();
                    out.swap();
                }
            }

        }catch(IOException | InterruptedException e) {
            ExpiServer.get().getServerGateway().playerPreLeaveAsync(owner);
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
        packetManager = new ServerPacketManager(owner);
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
