package com.astetyne.expirium.client.entity.animator;

import com.astetyne.expirium.client.entity.ClientEntity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class MoveableEntityAnimator extends EntityAnimator {

    protected boolean moving; // 0=idle right, 1=idle left, 2=run right, 3=run left
    private final Animation<TextureRegion> idleAnim, moveAnim;

    public MoveableEntityAnimator(ClientEntity e, Animation<TextureRegion> idleAnim, Animation<TextureRegion> moveAnim) {
        super(e);
        moving = false;
        this.idleAnim = idleAnim;
        this.moveAnim = moveAnim;
    }

    public void update() {

        super.update();

        Vector2 vel = entity.getVelocity();

        if(Math.abs(vel.x) <= 0.01 && moving) {
            moving = false;
            timer = 0;
        }else if(Math.abs(vel.x) >= 0.01 && !moving) {
            moving = true;
            timer = 0;
        }

    }

    public void draw(SpriteBatch batch) {

        Vector2 loc = entity.getLocation();

        if(moving) {
            if(entity.isLookingRight()) {
                batch.draw(moveAnim.getKeyFrame(timer), loc.x, loc.y - yOffset, w, h);
            }else {
                batch.draw(moveAnim.getKeyFrame(timer), loc.x, loc.y - yOffset, w/2, 0, w, h, -1, 1, 1);
            }
        }else {
            if(entity.isLookingRight()) {
                batch.draw(idleAnim.getKeyFrame(timer), loc.x, loc.y - yOffset, w, h);
            }else {
                batch.draw(idleAnim.getKeyFrame(timer), loc.x, loc.y - yOffset, w/2, 0, w, h, -1, 1, 1);
            }
        }

    }

}
