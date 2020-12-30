package com.astetyne.expirium.main.entity;

import com.astetyne.expirium.main.stages.GameStage;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.badlogic.gdx.math.Vector2;

public class PlayerEntity extends Entity {

    private final PlayerEntityAnimator animator;
    private String name;

    public PlayerEntity(int id, Vector2 loc, PacketInputStream in) {
        super(EntityType.PLAYER, id, loc, 0.9f, 1.25f);
        animator = new PlayerEntityAnimator(GameStage.get().getBatch(), this);
        readMeta(in);
    }

    @Override
    public void draw() {
        animator.draw();
    }

    @Override
    public void readMeta(PacketInputStream in) {
        name = in.getString();
    }
}