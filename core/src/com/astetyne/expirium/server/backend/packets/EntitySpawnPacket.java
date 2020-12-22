package com.astetyne.expirium.server.backend.packets;

import com.astetyne.expirium.server.api.entities.ExpiEntity;
import com.astetyne.expirium.server.backend.Packable;
import com.astetyne.expirium.server.backend.packables.PackableEntity;

import java.nio.ByteBuffer;

public class EntitySpawnPacket implements Packable {

    private final PackableEntity pe;

    public EntitySpawnPacket(ExpiEntity e) {
        pe = new PackableEntity(e);
    }


    @Override
    public int getPacketID() {
        return 20;
    }

    @Override
    public void populateWithData(ByteBuffer bb) {
        pe.populateWithData(bb);
    }
}
