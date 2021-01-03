package com.astetyne.expirium.server.backend;

import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.world.ExpiWorld;

import java.io.IOException;
import java.net.Socket;

public class ServerPlayerGateway extends TerminableLooper {

    private final GameServer gameServer;
    private final Socket client;
    private final Object joinLock;
    private PacketInputStream in;
    private PacketOutputStream out;
    private ServerPacketManager packetManager;
    private int traffic;
    private long time;

    public ServerPlayerGateway(Socket client) {
        this.gameServer = GameServer.get();
        this.client = client;
        joinLock = new Object();
        traffic = 0;
        time = 0;
    }

    @Override
    public void run() {

        try {

            System.out.println("New client connected.");

            in = new PacketInputStream(client.getInputStream());
            out = new PacketOutputStream(client.getOutputStream());

            packetManager = new ServerPacketManager(in, out);

            client.setSoTimeout(5000);
            int readBytes = in.fillBuffer();
            if(readBytes == -1) end();

            //System.out.println("Server reading init: "+readBytes);

            gameServer.playerPreJoinAsync(this);

            synchronized(joinLock) {
                joinLock.wait(); // wait until main looper recognize new player and populate init actions
            }

            in.reset();

            while(isRunning()) {
                out.flush();
                //System.out.println("Server flushing.");
                client.setSoTimeout(5000);
                readBytes = in.fillBuffer();
                if(readBytes == -1) end();//System.out.println("Server reading: "+readBytes+"\n"+in);

                traffic += readBytes;
                if(time + 1000 < System.currentTimeMillis()) {
                    time = System.currentTimeMillis();
                    System.out.println("Server traffic (bytes): "+traffic+" ping (ms): "+in.getPing()+" steps: "+ ExpiWorld.steps);
                    traffic = 0;
                }

                synchronized(gameServer.getTickLooper().getTickLock()) {
                    gameServer.getTickLooper().getTickLock().wait();
                    in.swap();
                    out.swap();
                }
            }

        }catch(IOException | InterruptedException e) {
            System.out.println("Channel with client failed.");
            try {
                client.close();
            }catch(IOException ignored) {
            }
            gameServer.playerPreLeaveAsync(this);
        }
    }

    @Override
    public void end() {
        super.end();
        try {
            client.close();
        }catch(IOException ignored) {
        }
        gameServer.playerPreLeaveAsync(this);
        System.out.println("Channel with client closed.");
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
