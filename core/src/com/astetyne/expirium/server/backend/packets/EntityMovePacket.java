package com.astetyne.expirium.server.backend.packets;

import com.astetyne.expirium.server.api.entities.ExpiEntity;
import com.astetyne.expirium.server.backend.Packable;

import java.nio.ByteBuffer;

public class EntityMovePacket implements Packable {

    private final int entityID;
    private final float x, y, v1, v2, angle, av;

    public EntityMovePacket(ExpiEntity e) {
        this.entityID = e.getID();
        this.x = e.getLocation().x;
        this.y = e.getLocation().y;
        this.v1 = e.getVelocity().x;
        this.v2 = e.getVelocity().y;
        this.angle = e.getBody().getAngle();
        this.av = e.getBody().getAngularVelocity();
    }

    @Override
    public int getPacketID() {
        return 19;
    }

    @Override
    public void populateWithData(ByteBuffer bb) {
        bb.putInt(entityID);
        bb.putFloat(x);
        bb.putFloat(y);
        bb.putFloat(v1);
        bb.putFloat(v2);
        bb.putFloat(angle);
        bb.putFloat(av);
    }
}
