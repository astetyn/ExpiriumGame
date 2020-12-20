package com.astetyne.main.net.client.packets;

import com.astetyne.server.backend.Packable;
import com.badlogic.gdx.math.Vector2;

import java.nio.ByteBuffer;

public class PlayerMovePacket implements Packable {

    private final float x, y, v1, v2;

    public PlayerMovePacket(Vector2 loc, Vector2 velocity) {
        x = loc.x;
        y = loc.y;
        v1 = velocity.x;
        v2 = velocity.y;
    }

    @Override
    public int getPacketID() {
        return 14;
    }

    @Override
    public byte[] toByteArray() {

        ByteBuffer bb = ByteBuffer.allocate(4 + 4*4);
        bb.putInt(getPacketID());

        bb.putFloat(x);
        bb.putFloat(y);
        bb.putFloat(v1);
        bb.putFloat(v2);

        return bb.array();
    }
}
