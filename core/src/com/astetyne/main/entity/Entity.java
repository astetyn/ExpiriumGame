package com.astetyne.main.entity;

import com.astetyne.main.world.Collidable;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

public class Entity implements Collidable {

    private final int ID;
    protected final Body body;
    private final Vector2 targetPosition;
    private float interpolateDelta;
    protected boolean onGround;
    private int collisions;

    public Entity(int id, Body body) {
        this.ID = id;
        this.body = body;
        targetPosition = new Vector2(body.getPosition());
        interpolateDelta = 0;
        onGround = true;
        collisions = 0;
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

    @Override
    public void onCollisionBegin(Fixture fix) {
        collisions++;
        onGround = true;
        System.out.println("GGG");
    }

    @Override
    public void onCollisionEnd(Fixture fix) {
        collisions--;
        if(collisions == 0) {
            onGround = false;
            System.out.println("AAA");
        }
    }
}
