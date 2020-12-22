package com.astetyne.expirium.server.backend.packets;

import com.astetyne.expirium.main.items.ItemType;
import com.astetyne.expirium.server.backend.Packable;

import java.nio.ByteBuffer;

public class ItemPickupPacket implements Packable {

    private final int itemID;

    public ItemPickupPacket(ItemType item) {
        this.itemID = item.getId();
    }

    @Override
    public int getPacketID() {
        return 22;
    }

    @Override
    public void populateWithData(ByteBuffer bb) {
        bb.putInt(itemID);
    }
}
