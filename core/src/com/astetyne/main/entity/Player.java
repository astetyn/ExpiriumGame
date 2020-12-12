package com.astetyne.main.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Player extends Entity {

    public Player(int id, Body body) {
        super(id, body);
    }

    public void move(float horz, float vert) {
        Vector2 center = body.getWorldCenter();
        float jump = 0;
        if(onGround) {
            if(vert >= 0.6f) {
                jump = vert;
            }
            if(Math.abs(body.getLinearVelocity().x) >= 3) {
                horz = 0;
            }
            body.applyLinearImpulse(50f * horz, 100f * jump, center.x, center.y, true);
        }
    }

}
