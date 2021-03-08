package com.astetyne.expirium.server.net;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.TerminableLooper;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

public class BroadcastSender extends TerminableLooper {

    @Override
    public void run() {


        while(isRunning()) {

           broadcast();

            try {
                Thread.sleep(1000);
            }catch(InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void broadcast() {

        try {

            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);

            String playersName = ExpiGame.get().getPlayerName();
            ByteBuffer bb = ByteBuffer.allocate(8 + playersName.length()*2); // 4-version, 4-string len, string...
            bb.putInt(playersName.length());
            for(char c : playersName.toCharArray()) {
                bb.putChar(c);
            }
            bb.putInt(Consts.VERSION);

            for(InetAddress address : listAllBroadcastAddresses()) {
                DatagramPacket packet = new DatagramPacket(bb.array(), bb.array().length, address, Consts.SERVER_PORT);
                socket.send(packet);
            }
            socket.close();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    List<InetAddress> listAllBroadcastAddresses() throws SocketException, UnknownHostException {
        List<InetAddress> broadcastList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while(interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if(networkInterface.isLoopback() || !networkInterface.isUp()) continue;

            networkInterface.getInterfaceAddresses().stream()
                    .map(InterfaceAddress::getBroadcast)
                    .filter(Objects::nonNull)
                    .forEach(broadcastList::add);
        }
        broadcastList.add(InetAddress.getByName("255.255.255.255"));
        return broadcastList;
    }
}
