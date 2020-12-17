package com.astetyne.main.net.netobjects;

import com.astetyne.main.entity.Entity;

public class SAdvPosition extends SVector {

    private final int id;
    private final float angle;
    private final SVector velocity;

    public SAdvPosition(Entity e) {
        super(e.getLocation());
        this.id = e.getID();
        this.velocity = new SVector(e.getVelocity());
        this.angle = e.getBody().getAngle();
    }

    public int getId() {
        return id;
    }

    public float getAngle() {
        return angle;
    }

    public SVector getVelocity() {
        return velocity;
    }
}
