package com.astetyne.expirium.server.net;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.backend.TerminableLooper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class MulticastSender extends TerminableLooper {

    @Override
    public void run() {

        try {

            InetAddress group = InetAddress.getByName("234.14.14.14");
            DatagramSocket socket = new DatagramSocket();

            String playersName = ExpiGame.get().getPlayerName();
            ByteBuffer bb = ByteBuffer.allocate(8 + playersName.length()*2); // 4-version, 4-string len, string...
            bb.putInt(playersName.length());
            for(char c : playersName.toCharArray()) {
                bb.putChar(c);
            }
            bb.putInt(Consts.VERSION);

            while(isRunning()) {

                DatagramPacket packet = new DatagramPacket(bb.array(), bb.array().length, group, Consts.SERVER_PORT);
                socket.send(packet);

                Thread.sleep(1000);
            }

            socket.close();

        }catch(IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
