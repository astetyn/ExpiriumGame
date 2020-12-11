package com.astetyne.main.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Entity {

    private final int ID;
    private final Body body;
    private final Vector2 targetPosition;
    private float interpolateDelta;

    public Entity(int id, Body body) {
        this.ID = id;
        this.body = body;
        targetPosition = new Vector2(body.getPosition());
        interpolateDelta = 0;
    }

    public Vector2 getLocation() {
        return body.getPosition();
    }

    public Vector2 getVelocity() {
        return body.getLinearVelocity();
    }

    public Body getBody() {
        return body;
    }

    public int getID() {
        return ID;
    }

    public Vector2 getTargetPosition() {
        return targetPosition;
    }

    public void setInterpolateDelta(float interpolateDelta) {
        this.interpolateDelta = interpolateDelta;
    }

    public float getInterpolateDelta() {
        return interpolateDelta;
    }
}
