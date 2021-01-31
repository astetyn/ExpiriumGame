package com.astetyne.expirium.client.entity.animator;

import com.astetyne.expirium.client.entity.Entity;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class EntityAnimator {

    protected final Entity entity;
    protected float timer;
    protected float w, h;
    protected long lastInjureTime;
    protected float lastInjureValue;

    public EntityAnimator(Entity entity) {
        this.entity = entity;
        timer = 0;
        this.w = entity.getType().getWidth();
        this.h = entity.getType().getHeight();
        lastInjureTime = 0;
        lastInjureValue = 0;
    }

    public abstract void draw(SpriteBatch batch);

    /** Subclasses should override this method if they listen to custom animations but always call this super method.*/
    public void onEntityAnim(EntityAnimation anim, PacketInputStream in) {
        if(anim == EntityAnimation.INJURE) {
            lastInjureValue = in.getFloat();
            lastInjureTime = System.currentTimeMillis();
        }
    }

}
