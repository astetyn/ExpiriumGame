package com.astetyne.expirium.client.net;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.screens.GameScreen;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.backend.TerminableLooper;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.astetyne.expirium.server.net.PacketOutputStream;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientGateway extends TerminableLooper {

    private Socket socket;
    private InetAddress address;
    private PacketInputStream in;
    private PacketOutputStream out;
    private ClientPacketManager packetManager;
    private final Object nextPacketsLock;
    private int traffic;
    private long time;
    private boolean nextPacketsAvailable;
    private final ClientFailListener failListener;

    public ClientGateway(ClientFailListener listener) {
        nextPacketsLock = new Object();
        traffic = 0;
        time = 0;
        nextPacketsAvailable = false;
        failListener = listener;
    }

    @Override
    public void run() {

        System.out.println("Client connecting to: "+address+":"+Consts.SERVER_PORT);

        long startT = System.currentTimeMillis();
        while(startT + 10000 > System.currentTimeMillis()) {
            System.out.println("Next connection attempt...");
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(address, Consts.SERVER_PORT), 10000);
                break;
            } catch(IOException ignored) {}
            try {
                Thread.sleep(1000);
            }catch(InterruptedException ignored) {}
        }
        if(!socket.isConnected()) {
            failListener.onClientFail("Can't connect to server.\n Are you on the same network?");
            return;
        }

        try {

            socket.setTcpNoDelay(true);

            in = new PacketInputStream(socket.getInputStream());
            out = new PacketOutputStream(socket.getOutputStream());

            packetManager = new ClientPacketManager(in, out);

            packetManager.putJoinReqPacket(ExpiGame.get().getPlayerName());
            out.swap();
            out.flush();
            out.reset();

            while(isRunning()) {

                int readBytes = in.fillBuffer();
                out.flush();

                traffic += readBytes;
                if(time + 5000 < System.currentTimeMillis()) {
                    time = System.currentTimeMillis();
                    System.out.println("Client traffic (bytes): "+traffic+" ping (ms): "+in.getPing());
                    traffic = 0;
                }

                synchronized(nextPacketsLock) {
                    nextPacketsAvailable = true;
                    nextPacketsLock.wait();
                }
            }
        }catch(IOException | InterruptedException e) {
            if(!isRunning()) return;
            try {
                socket.close();
            }catch(IOException ignored) {}
            failListener.onClientFail("You were disconnected due to connection issue.");
        }
    }

    /**
     * Call this from main thread every frame. If new packet is available, out buffer will be flushed
     * and incoming packets will be resolved.
     */
    public void update() {

        synchronized(nextPacketsLock) {
            if(!nextPacketsAvailable) return;
            nextPacketsAvailable = false;
        }

        if(GameScreen.get() != null) {
            packetManager.putTSPacket();
        }

        out.swap();
        in.swap();

        synchronized(nextPacketsLock) {
            nextPacketsLock.notify();
        }

        packetManager.processIncomingPackets();
    }

    /**
     * Gateway will attempt to connect to server at given address. This will create new thread.
     * @param address Server address.
     */
    public void connectToServer(InetAddress address) {

        running = true;
        this.address = address;

        if(socket != null && !socket.isClosed()) {
            close();
        }

        Thread t = new Thread(this);
        t.setName("Client gateway");
        t.start();
    }

    /**
     * Call this when client should disconnect from server and release resources. Can be reopened with
     * connectToServer().
     */
    public void close() {

        try {
            stop();
            if(socket != null) socket.close();
        }catch(IOException ignored) {}

        synchronized(nextPacketsLock) {
            nextPacketsLock.notify();
        }
        // after this I should be confident that client thread will be stopped
        in.reset();
        out.reset();
        System.out.println("Client closed.");
    }

    public ClientPacketManager getManager() {
        return packetManager;
    }

    public PacketInputStream getIn() {
        return in;
    }

    public PacketOutputStream getOut() {
        return out;
    }

}
