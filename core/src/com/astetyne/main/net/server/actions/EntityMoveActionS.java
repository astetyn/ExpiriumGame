package com.astetyne.main.net.server.actions;

import com.astetyne.main.net.netobjects.SVector;

public class EntityMoveActionS extends ServerAction {

    private final int entityID;
    private final SVector newLocation;
    private final SVector velocity;

    public EntityMoveActionS(int entityID, SVector newLocation, SVector velocity) {
        this.entityID = entityID;
        this.newLocation = newLocation;
        this.velocity = velocity;
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
}
