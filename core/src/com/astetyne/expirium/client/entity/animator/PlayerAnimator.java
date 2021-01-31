package com.astetyne.expirium.client.entity.animator;

import com.astetyne.expirium.client.Res;
import com.astetyne.expirium.client.entity.Entity;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PlayerAnimator extends MoveableEntityAnimator {

    private long lastInteractTime;
    private final Animation<TextureRegion> interactAnim;

    public PlayerAnimator(Entity e, Animation<TextureRegion> idleAnim, Animation<TextureRegion> moveAnim) {
        super(e, idleAnim, moveAnim);
        interactAnim = Res.PLAYER_INTERACT_ANIM;
        lastInteractTime = 0;
    }

    public void draw(SpriteBatch batch) {

        //todo: skontrolovat, ci stale prebieha interact anim, ak nie, tak zavolat super, ak ano, tak nakreslit

    }

    public void onEntityAnim(EntityAnimation anim, PacketInputStream in) {
        if(anim == EntityAnimation.INTERACT) {
            lastInteractTime = System.currentTimeMillis();
        }else {
            super.onEntityAnim(anim, in);
        }
    }

}
