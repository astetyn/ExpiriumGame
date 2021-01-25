package com.astetyne.expirium.client.net;

import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.backend.TerminableLooper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;

public class MulticastListener extends TerminableLooper {

    private MulticastSocket socket;

    @Override
    public void run() {

        try {

            byte[] buffer = new byte[1024];
            socket = new MulticastSocket(1414);
            InetAddress group = InetAddress.getByName(Consts.MULTICAST_ADDRESS);
            socket.joinGroup(group);

            while(isRunning()) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                if(packet.getLength() < 4) continue;
                ByteBuffer bb = ByteBuffer.wrap(packet.getData(), packet.getOffset(), packet.getLength());

                int serverVersion = bb.getInt();
                InetAddress serverAddress = packet.getAddress();

                System.out.println("Server observed on: "+serverAddress+" version: "+serverVersion);
            }

            socket.leaveGroup(group);
            socket.close();

        }catch(IOException ignored) {}

    }

    @Override
    public void end() {
        super.end();
        if(socket != null) {
            socket.close();
        }
    }
}
