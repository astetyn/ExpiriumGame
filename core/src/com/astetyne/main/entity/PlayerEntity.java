package com.astetyne.main.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;

public class PlayerEntity extends Entity {

    private final PlayerEntityAnimator animator;

    public PlayerEntity(int id, Body body, SpriteBatch batch) {
        super(id, body, 0.9f, 1.25f);
        animator = new PlayerEntityAnimator(batch, this);
    }

    @Override
    public void draw() {
        animator.draw();
    }
}
