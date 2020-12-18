package com.astetyne.main.net.server.actions;

import com.astetyne.main.entity.Entity;
import com.astetyne.main.net.netobjects.MessageAction;
import com.astetyne.main.net.netobjects.SVector;

public class EntityMoveActionCS extends MessageAction {

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
}
