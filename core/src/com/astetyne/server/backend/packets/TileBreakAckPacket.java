package com.astetyne.server.backend.packets;

import com.astetyne.server.api.entities.ExpiDroppedItem;
import com.astetyne.server.backend.Packable;

import java.nio.ByteBuffer;

public class TileBreakAckPacket implements Packable {

    private final int chunkID, x, y;
    private final float itemAngleVel;
    private final int entityID;
    private final int itemTypeID;

    public TileBreakAckPacket(int chunkID, int x, int y, ExpiDroppedItem entity) {
        this.chunkID = chunkID;
        this.x = x;
        this.y = y;
        itemAngleVel = entity.getBody().getAngularVelocity();
        this.entityID = entity.getID();
        this.itemTypeID = entity.getType().getID();
    }

    @Override
    public int getPacketID() {
        return 17;
    }

    @Override
    public byte[] toByteArray() {

        ByteBuffer bb = ByteBuffer.allocate(4 + 6*4);
        bb.putInt(getPacketID());

        bb.putInt(chunkID);
        bb.putInt(x);
        bb.putInt(y);
        bb.putInt(entityID);
        bb.putInt(itemTypeID);
        bb.putFloat(itemAngleVel);

        return bb.array();
    }
}
