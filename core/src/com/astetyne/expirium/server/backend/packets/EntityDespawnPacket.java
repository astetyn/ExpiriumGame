package com.astetyne.expirium.server.backend.packets;

import com.astetyne.expirium.server.api.entities.ExpiEntity;
import com.astetyne.expirium.server.backend.Packable;

import java.nio.ByteBuffer;

public class EntityDespawnPacket implements Packable {

    private final int eID;

    public EntityDespawnPacket(ExpiEntity e) {
        this.eID = e.getID();
    }

    @Override
    public int getPacketID() {
        return 21;
    }

    @Override
    public void populateWithData(ByteBuffer bb) {
        bb.putInt(eID);
    }
}
