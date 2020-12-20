package com.astetyne.main.net.client.packets;

import com.astetyne.server.backend.Packable;

import java.nio.ByteBuffer;

public class JoinRequestPacket implements Packable {

    private final String name;

    public JoinRequestPacket(String name) {
        this.name = name;
    }

    public JoinRequestPacket(ByteBuffer bb) {
        StringBuilder s = new StringBuilder();
        int len = bb.getInt();
        for(int i = 0; i < len; i++) {
            s.append(bb.getChar());
        }
        this.name = s.toString();
    }

    public String getName() {
        return name;
    }

    @Override
    public int getPacketID() {
        return 10;
    }

    @Override
    public byte[] toByteArray() {

        char[] charArr = name.toCharArray();

        ByteBuffer bb = ByteBuffer.allocate(charArr.length+8);
        bb.putInt(getPacketID());
        bb.putInt(charArr.length);
        for(char c : name.toCharArray()) {
            bb.putChar(c);
        }
        return bb.array();
    }
}
