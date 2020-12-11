package com.astetyne.main.net.client.actions;

import com.astetyne.main.net.netobjects.SVector;

public class PlayerMoveActionC extends ClientAction {

    private final SVector newLocation;
    private final SVector velocity;

    public PlayerMoveActionC(SVector newLocation, SVector velocity) {
        this.newLocation = newLocation;
        this.velocity = velocity;
    }

    public SVector getNewLocation() {
        return newLocation;
    }

    public SVector getVelocity() {
        return velocity;
    }
}
