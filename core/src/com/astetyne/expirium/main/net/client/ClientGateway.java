package com.astetyne.expirium.main.net.client;

import com.astetyne.expirium.main.ExpiGame;
import com.astetyne.expirium.main.screens.Gatewayable;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.astetyne.expirium.server.backend.PacketOutputStream;
import com.astetyne.expirium.server.backend.TerminableLooper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientGateway extends TerminableLooper {

    private Socket socket;
    private final ExpiGame game;
    private String ipAddress;
    private PacketInputStream in;
    private PacketOutputStream out;
    private ClientPacketManager packetManager;
    private final Object nextReadLock;
    private int traffic;
    private long time;

    public ClientGateway() {
        game = ExpiGame.get();
        ipAddress = "127.0.0.1";
        nextReadLock = new Object();
        traffic = 0;
        time = 0;
    }

    @Override
    public void run() {

        System.out.println("Client connecting...");

        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(ipAddress, Consts.SERVER_PORT), 10000);
        } catch(IOException e) {
            System.out.println("Exception during connecting to server.");
            if(game.getScreen() instanceof Gatewayable) {
                ((Gatewayable)game.getScreen()).onServerFail();
            }
            return;
        }

        try {

            socket.setTcpNoDelay(true);

            System.out.println("Connection established.");

            in = new PacketInputStream(socket.getInputStream());
            out = new PacketOutputStream(socket.getOutputStream());

            packetManager = new ClientPacketManager(in, out);

            packetManager.putJoinReqPacket(ExpiGame.get().getPlayerName());
            out.swap();
            out.flush();
            out.reset();
            //System.out.println("Client flushing.");

            while(isRunning()) {

                socket.setSoTimeout(5000);
                int readBytes = in.fillBuffer();//System.out.println("Client reading: "+readBytes);
                out.flush(); //System.out.println("Client flushing.");

                traffic += readBytes;
                if(time + 5000 < System.currentTimeMillis()) {
                    time = System.currentTimeMillis();
                    System.out.println("Client traffic (bytes): "+traffic+" ping (ms): "+in.getPing());
                    traffic = 0;
                }

                synchronized(nextReadLock) {
                    game.notifyServerUpdate();
                    nextReadLock.wait();
                }
            }
        }catch(IOException e) {
            System.out.println("Exception during messaging with server.");
            if(game.getScreen() instanceof Gatewayable) {
                ((Gatewayable)game.getScreen()).onServerFail();
            }
        }catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void end() {
        super.end();
        try {
            if(socket != null) {
                socket.close();
            }
        }catch(IOException ignored) {
        }
        System.out.println("Client successfully closed socket.");
    }

    // call this only once per server tick as active buffer will be switched and overwritten.
    public void swapBuffers() {
        out.swap();
        in.swap();
        synchronized(nextReadLock) {
            nextReadLock.notify();
        }
    }

    public void setIpAddress(String address) {
        ipAddress = address;
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
