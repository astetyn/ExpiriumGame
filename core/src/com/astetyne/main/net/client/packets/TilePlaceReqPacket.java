package com.astetyne.main.net.client.packets;

import com.astetyne.main.items.ItemType;
import com.astetyne.server.backend.Packable;

import java.nio.ByteBuffer;

public class TilePlaceReqPacket implements Packable {

    private final int chunkID, x, y;
    private final ItemType placedItem;

    public TilePlaceReqPacket(int chunkID, int x, int y, ItemType placedItem) {
        this.chunkID = chunkID;
        this.x = x;
        this.y = y;
        this.placedItem = placedItem;
    }

    @Override
    public int getPacketID() {
        return 16;
    }

    @Override
    public byte[] toByteArray() {

        ByteBuffer bb = ByteBuffer.allocate(4 + 4*4);
        bb.putInt(getPacketID());
        bb.putInt(chunkID);
        bb.putInt(x);
        bb.putInt(y);
        bb.putInt(placedItem.getId());

        return new byte[0];
    }
}
