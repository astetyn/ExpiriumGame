package com.astetyne.server.backend.packets;

import com.astetyne.main.entity.Entity;
import com.astetyne.main.net.netobjects.SVector;
import com.astetyne.server.backend.Packable;

public class EntityMoveActionCS implements Packable {

    private final int entityID;
    private final SVector newLocation;
    private final SVector velocity;
    private final float angle;

    public EntityMoveActionCS(Entity e) {
        this.entityID = e.getID();
        this.newLocation = new SVector(e.getLocation());
        this.velocity = new SVector(e.getVelocity());
        this.angle = e.getBody().getAngle();
    }

    public EntityMoveActionCS(int entityID, SVector newLocation, SVector velocity, float angle) {
        this.entityID = entityID;
        this.newLocation = newLocation;
        this.velocity = velocity;
        this.angle = angle;
    }

    public int getEntityID() {
        return entityID;
    }

    public SVector getNewLocation() {
        return newLocation;
    }

    public SVector getVelocity() {
        return velocity;
    }

    public float getAngle() {
        return angle;
    }

    @Override
    public int getPacketID() {
        return 0;
    }

    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }
}
