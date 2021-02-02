package com.astetyne.expirium.client.entity.animator;

import com.astetyne.expirium.client.entity.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class MoveableEntityAnimator extends EntityAnimator {

    protected byte lastState; // 0=idle right, 1=idle left, 2=run right, 3=run left
    private final Animation<TextureRegion> idleAnim, moveAnim;

    public MoveableEntityAnimator(Entity e, Animation<TextureRegion> idleAnim, Animation<TextureRegion> moveAnim) {
        super(e);
        lastState = 0;
        this.idleAnim = idleAnim;
        this.moveAnim = moveAnim;
    }

    public void update() {

        super.update();

        Vector2 vel = entity.getVelocity();

        if(Math.abs(vel.x) <= 0.1) {
            // idle right
            if(lastState == 2) {
                lastState = 0;
                timer = 0;
            // idle left
            }else if(lastState == 3) {
                lastState = 1;
                timer = 0;
            }
        }else if(vel.x > 0 && lastState != 2) {
            // run right
            lastState = 2;
            timer = 0;
        }else if(vel.x < 0 && lastState != 3){
            // run left
            lastState = 3;
            timer = 0;
        }

    }

    public void draw(SpriteBatch batch) {

        Vector2 loc = entity.getLocation();

        if(lastState == 0) {
            batch.draw(idleAnim.getKeyFrame(timer), loc.x, loc.y - yOffset, w, h);
        }else if(lastState == 1) {
            batch.draw(idleAnim.getKeyFrame(timer), loc.x, loc.y - yOffset, w/2, 0, w, h, -1, 1, 1);
        }else if(lastState == 2) {
            batch.draw(moveAnim.getKeyFrame(timer), loc.x, loc.y - yOffset, w, h);
        }else if(lastState == 3) {
            batch.draw(moveAnim.getKeyFrame(timer), loc.x, loc.y - yOffset, w/2, 0, w, h, -1, 1, 1);
        }

    }

}
