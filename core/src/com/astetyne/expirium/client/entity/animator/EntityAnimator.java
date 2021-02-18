package com.astetyne.expirium.client.entity.animator;

import com.astetyne.expirium.client.entity.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class EntityAnimator {

    protected static final float yOffset = 0.04f;

    protected final Entity entity;
    protected float timer;
    protected float w, h;

    public EntityAnimator(Entity entity) {
        this.entity = entity;
        timer = 0;
        this.w = entity.getType().getWidth();
        this.h = entity.getType().getHeight();
    }

    public void update() {
        timer += Gdx.graphics.getDeltaTime();
    }

    public abstract void draw(SpriteBatch batch);

}
