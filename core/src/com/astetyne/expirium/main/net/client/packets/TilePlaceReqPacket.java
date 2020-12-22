package com.astetyne.expirium.main.net.client.packets;

import com.astetyne.expirium.main.items.ItemType;
import com.astetyne.expirium.server.backend.Packable;

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
    public void populateWithData(ByteBuffer bb) {
        bb.putInt(chunkID);
        bb.putInt(x);
        bb.putInt(y);
        bb.putInt(placedItem.getId());
    }
}
