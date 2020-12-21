package com.astetyne.main.entity;

import com.astetyne.main.stages.GameStage;
import com.badlogic.gdx.math.Vector2;

public class PlayerEntity extends Entity {

    private final PlayerEntityAnimator animator;

    public PlayerEntity(int id, Vector2 loc) {
        super(EntityType.PLAYER, id, loc, 0.9f, 1.25f);
        animator = new PlayerEntityAnimator(GameStage.get().getBatch(), this);
    }

    @Override
    public void draw() {
        animator.draw();
    }

}
