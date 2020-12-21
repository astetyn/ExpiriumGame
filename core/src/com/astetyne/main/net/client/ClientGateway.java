package com.astetyne.main.net.client;

import com.astetyne.main.ExpiriumGame;
import com.astetyne.main.net.client.packets.JoinReqPacket;
import com.astetyne.main.utils.Constants;
import com.astetyne.server.backend.IncomingPacket;
import com.astetyne.server.backend.Packable;
import com.astetyne.server.backend.TerminableLooper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientGateway extends TerminableLooper {

    private Socket socket;
    private final List<Packable> clientSubPackets;
    private final List<IncomingPacket> serverIncomingPackets;
    private final ExpiriumGame game;
    private String ipAddress;
    private final byte[] inputBuffer;
    private final ByteBuffer outputBuffer;

    public ClientGateway() {
        clientSubPackets = new ArrayList<>();
        serverIncomingPackets = new ArrayList<>();
        game = ExpiriumGame.get();
        ipAddress = "127.0.0.1";
        inputBuffer = new byte[262144];
        outputBuffer = ByteBuffer.allocate(262144);
    }

    @Override
    public void run() {

        System.out.println("Client connecting...");

        try {

            socket = new Socket();
            socket.setPerformancePreferences(0,10,0);
            socket.connect(new InetSocketAddress(ipAddress, Constants.SERVER_PORT), 10000);

        } catch(IOException e) {
            // cant connect to the server
            System.out.println("Exception during connecting to server.");
            game.getCurrentStage().onServerFail();
            return;
        }

        try {

            System.out.println("Connection established.");

            BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());

            JoinReqPacket jra = new JoinReqPacket(game.getPlayerName());
            outputBuffer.putInt(jra.getPacketID());
            jra.populateWithData(outputBuffer);
            bos.write(outputBuffer.array(), 0, outputBuffer.position());
            bos.flush();
            outputBuffer.clear();

            while(isRunning()) {

                socket.setSoTimeout(5000);
                int readBytes = bis.read(inputBuffer);

                synchronized(serverIncomingPackets) {
                    serverIncomingPackets.add(new IncomingPacket(Arrays.copyOf(inputBuffer, readBytes)));
                }

                List<Packable> copy;
                synchronized(clientSubPackets) {
                    copy = new ArrayList<>(clientSubPackets);
                    clientSubPackets.clear();
                }

                outputBuffer.putInt(copy.size());
                for(Packable p : copy) {
                    outputBuffer.putInt(p.getPacketID());
                    p.populateWithData(outputBuffer);
                }
                bos.write(outputBuffer.array(), 0, outputBuffer.position());
                bos.flush();
                outputBuffer.clear();

                game.notifyServerUpdate();

            }

        }catch(IOException e) {
            // exception during playing on server
            System.out.println("Exception during messaging with server.");
            game.getCurrentStage().onServerFail();
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

    public void addSubPacket(Packable subPacket) {
        synchronized(clientSubPackets) {
            clientSubPackets.add(subPacket);
        }
    }

    // call this only once per server tick as list will be cleared after the call
    public List<IncomingPacket> getServerPackets() {
        synchronized(serverIncomingPackets) {
            List<IncomingPacket> copy = new ArrayList<>(serverIncomingPackets);
            serverIncomingPackets.clear();
            return copy;
        }
    }

    public void setIpAddress(String address) {
        ipAddress = address;
    }

}
