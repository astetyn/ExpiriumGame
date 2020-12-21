package com.astetyne.main.entity;

import com.astetyne.main.stages.GameStage;
import com.badlogic.gdx.math.Vector2;

import java.nio.ByteBuffer;

public class PlayerEntity extends Entity {

    private final PlayerEntityAnimator animator;
    private String name;

    public PlayerEntity(int id, Vector2 loc, ByteBuffer bb) {
        super(EntityType.PLAYER, id, loc, 0.9f, 1.25f);
        animator = new PlayerEntityAnimator(GameStage.get().getBatch(), this);
        readMeta(bb);
    }

    @Override
    public void draw() {
        animator.draw();
    }

    @Override
    public void readMeta(ByteBuffer bb) {
        int nameLen = bb.getInt();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < nameLen; i++) {
            sb.append(bb.getChar());
        }
        name = sb.toString();
    }
}
