package com.astetyne.expirium.client.entity.animator;

import com.astetyne.expirium.client.entity.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class EntityAnimator {

    protected static final float yOffset = 0.04f;

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

    public void update() {
        timer += Gdx.graphics.getDeltaTime();
    }

    public abstract void draw(SpriteBatch batch);

    public void injure(float damageValue) {
        lastInjureValue = damageValue;
        lastInjureTime = System.currentTimeMillis();
    }

}
