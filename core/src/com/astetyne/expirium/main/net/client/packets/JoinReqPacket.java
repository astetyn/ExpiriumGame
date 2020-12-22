package com.astetyne.expirium.main.net.client.packets;

import com.astetyne.expirium.server.backend.Packable;

import java.nio.ByteBuffer;

public class JoinReqPacket implements Packable {

    private final char[] name;

    public JoinReqPacket(String name) {
        this.name = name.toCharArray();
    }

    @Override
    public int getPacketID() {
        return 10;
    }

    @Override
    public void populateWithData(ByteBuffer bb) {
        bb.putInt(name.length);
        for(char c : name) {
            bb.putChar(c);
        }
    }
}
