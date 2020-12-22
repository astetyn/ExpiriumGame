package com.astetyne.expirium.server.backend;

import com.astetyne.expirium.server.GameServer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerPlayerGateway extends TerminableLooper {

    private final GameServer gameServer;
    private final Socket client;
    private final List<Packable> serverSubPackets;
    private final List<IncomingPacket> clientIncomingPackets;
    private final Object joinLock;
    private final byte[] inputBuffer;
    private final ByteBuffer outputBuffer;

    public ServerPlayerGateway(Socket client) {
        this.gameServer = GameServer.get();
        this.client = client;
        serverSubPackets = new ArrayList<>();
        clientIncomingPackets = new ArrayList<>();
        joinLock = new Object();
        inputBuffer = new byte[65536];
        outputBuffer = ByteBuffer.allocate(262144);
    }

    @Override
    public void run() {

        try {

            System.out.println("New client connected.");

            BufferedInputStream bis = new BufferedInputStream(client.getInputStream());
            BufferedOutputStream bos = new BufferedOutputStream(client.getOutputStream());

            client.setSoTimeout(5000);
            int readBytes = bis.read(inputBuffer);

            synchronized(clientIncomingPackets) {
                clientIncomingPackets.add(new IncomingPacket(Arrays.copyOf(inputBuffer, readBytes)));
            }

            gameServer.playerPreJoinAsync(this);

            synchronized(joinLock) {
                joinLock.wait(); // wait until main looper recognize new player and populate init actions
            }

            while(isRunning()) {

                List<Packable> copy;
                synchronized(serverSubPackets) {
                    copy = new ArrayList<>(serverSubPackets);
                    serverSubPackets.clear();
                }

                outputBuffer.putInt(copy.size());
                for(Packable p : copy) {
                    outputBuffer.putInt(p.getPacketID());
                    p.populateWithData(outputBuffer);
                }
                bos.write(outputBuffer.array(), 0, outputBuffer.position());
                bos.flush();
                outputBuffer.clear();

                client.setSoTimeout(5000);
                readBytes = bis.read(inputBuffer);
                if(readBytes == -1) end();
                //System.out.println("S: read: "+readBytes);
                synchronized(clientIncomingPackets) {
                    clientIncomingPackets.add(new IncomingPacket(Arrays.copyOf(inputBuffer, readBytes)));
                }

                synchronized(gameServer.getTickLooper().getTickLock()) {
                    gameServer.getTickLooper().getTickLock().wait();
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
        System.out.println("Channel with client closed.");
    }

    public void addSubPacket(Packable subPacket) {
        synchronized(serverSubPackets) {
            serverSubPackets.add(subPacket);
        }
    }

    public void addSubPackets(List<Packable> subPackets) {
        synchronized(serverSubPackets) {
            serverSubPackets.addAll(subPackets);
        }
    }

    // call this only once per server tick as list will be cleared after the call
    public List<IncomingPacket> getClientIncomingPackets() {
        synchronized(clientIncomingPackets) {
            List<IncomingPacket> copy = new ArrayList<>(clientIncomingPackets);
            clientIncomingPackets.clear();
            return copy;
        }
    }

    public Object getJoinLock() {
        return joinLock;
    }
}
