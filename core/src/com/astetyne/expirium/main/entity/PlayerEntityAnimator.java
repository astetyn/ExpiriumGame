package com.astetyne.expirium.main.entity;

import com.astetyne.expirium.main.Res;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class PlayerEntityAnimator {

    private final SpriteBatch batch;
    private final PlayerEntity player;

    private float timer;
    private int lastState;

    public PlayerEntityAnimator(SpriteBatch batch, PlayerEntity player) {

        this.batch = batch;
        this.player = player;

        timer = 0;
        lastState = 0;

    }

    public void draw() {

        timer += Gdx.graphics.getDeltaTime();

        Vector2 vel = player.getVelocity();
        Vector2 loc = player.getLocation();

        if(Math.abs(vel.x) <= 0.1) {

            // idle right
            if(lastState == 0 || lastState == 2) {
                if(lastState == 2) {
                    lastState = 0;
                    timer = 0;
                }
                batch.draw(Res.PLAYER_IDLE_ANIM_R.getKeyFrame(timer), loc.x, loc.y, 0.9f, 1.25f);

                // idle left
            }else if(lastState == 1 || lastState == 3) {
                if(lastState == 3) {
                    lastState = 1;
                    timer = 0;
                }
                batch.draw(Res.PLAYER_IDLE_ANIM_L.getKeyFrame(timer), loc.x, loc.y, 0.9f, 1.25f);
            }

            // run right
        }else if(vel.x > 0) {
            if(lastState != 2) {
                lastState = 2;
                timer = 0;
            }
            batch.draw(Res.PLAYER_RUN_ANIM_R.getKeyFrame(timer), loc.x, loc.y, 0.9f, 1.25f);

            // run left
        }else {
            if(lastState != 3) {
                lastState = 3;
                timer = 0;
            }
            batch.draw(Res.PLAYER_RUN_ANIM_L.getKeyFrame(timer), loc.x, loc.y, 0.9f, 1.25f);
        }

    }

}
