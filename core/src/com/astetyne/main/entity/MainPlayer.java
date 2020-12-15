package com.astetyne.main.entity;

import com.astetyne.main.gui.ThumbStick;
import com.astetyne.main.net.client.actions.PlayerMoveActionC;
import com.astetyne.main.net.netobjects.SVector;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class MainPlayer extends Entity {

    private final MainPlayerAnimator animator;
    private final ThumbStick thumbStick;

    public MainPlayer(int id, Body body, SpriteBatch batch, ThumbStick thumbStick) {
        super(id, body);
        animator = new MainPlayerAnimator(batch, this, thumbStick);
        this.thumbStick = thumbStick;
    }

    public void draw() {
        animator.draw();
    }

    public void move() {

        float vert = thumbStick.getVert();
        float horz = thumbStick.getHorz();

        Vector2 center = body.getWorldCenter();
        float jump = 0;
        if(onGround) {
            if(body.getLinearVelocity().y < 5 && vert >= 0.6f) {
                jump = 1;
            }
        }
        if((body.getLinearVelocity().x >= 3 && horz > 0) || (body.getLinearVelocity().x <= -3 && horz < 0)) {
            horz = 0;
        }
        body.applyLinearImpulse(0, 60*jump, center.x, center.y, true);
        body.applyForceToCenter(1000f * horz, 0, true);

    }

    public PlayerMoveActionC generateMoveAction() {
        return new PlayerMoveActionC(new SVector(getLocation()), new SVector(getVelocity()));
    }

}
