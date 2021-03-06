package com.astetyne.expirium.client.net;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.TerminableLooper;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class MulticastListener extends TerminableLooper {

    private MulticastSocket socket;
    private boolean serversChanged;
    private final List<AvailableServer> availableServers;

    public MulticastListener() {
        serversChanged = false;
        availableServers = new ArrayList<>();
    }

    @Override
    public void run() {

        try {

            byte[] buffer = new byte[1024];
            socket = new MulticastSocket(Consts.SERVER_PORT);
            InetAddress group = InetAddress.getByName(Consts.MULTICAST_ADDRESS);
            NetworkInterface networkInterface = getWlanEth();
            if(networkInterface != null) socket.setNetworkInterface(networkInterface);
            socket.joinGroup(group);

            outer:
            while(isRunning()) {

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                if(packet.getLength() < 8) continue;
                ByteBuffer bb = ByteBuffer.wrap(packet.getData(), packet.getOffset(), packet.getLength());

                int len = bb.getInt();
                if(packet.getLength() < 8 + len) continue;
                StringBuilder playerName = new StringBuilder();
                for(int i = 0; i < len; i++) {
                    playerName.append(bb.getChar());
                }
                int version = bb.getInt();
                InetAddress address = packet.getAddress();

                for(AvailableServer server : availableServers) {
                    if(server.address.getHostAddress().equals(address.getHostAddress())) {
                        server.lastPing = System.currentTimeMillis();
                        continue outer;
                    }
                }
                if(ExpiGame.get().getPlayerName().equals(playerName.toString())) continue;
                availableServers.add(new AvailableServer(playerName.toString(), version, address));
                serversChanged = true;
            }

            socket.leaveGroup(group);
            socket.close();

        }catch(IOException ignored) {}

    }

    @Override
    public void stop() {
        super.stop();
        if(socket != null) {
            socket.close();
        }
    }

    public boolean hasChanged() {
        return serversChanged;
    }

    public List<AvailableServer> getAvailableServers() {
        serversChanged = false;
        availableServers.removeIf(server -> server.lastPing + 5000 < System.currentTimeMillis());
        return availableServers;
    }

    public static class AvailableServer {

        public String owner;
        public int version;
        public InetAddress address;
        public long lastPing;

        public AvailableServer(String owner, int version, InetAddress address) {
            this.owner = owner;
            this.version = version;
            this.address = address;
            lastPing = System.currentTimeMillis();
        }
    }

    public static NetworkInterface getWlanEth() {
        Enumeration<NetworkInterface> enumeration = null;
        try {
            enumeration = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if(enumeration == null) return null;
        NetworkInterface wlan0 = null;
        StringBuilder sb = new StringBuilder();
        while (enumeration.hasMoreElements()) {
            wlan0 = enumeration.nextElement();
            sb.append(wlan0.getName() + " ");
            if (wlan0.getName().equals("wlan0")) {
                //there is probably a better way to find ethernet interface
                System.out.println("wlan0 found");
                return wlan0;
            }
        }

        return null;
    }
}
