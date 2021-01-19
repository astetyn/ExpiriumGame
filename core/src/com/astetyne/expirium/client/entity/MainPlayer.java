package com.astetyne.expirium.client.entity;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class MainPlayer extends Entity {

    private final PlayerEntityAnimator animator;

    public MainPlayer(int id, Vector2 loc) {
        super(EntityType.PLAYER, id, loc, 0.9f, 1.25f);
        animator = new PlayerEntityAnimator(ExpiGame.get().getBatch(), this);
    }

    public void draw(SpriteBatch batch) {
        animator.draw();
    }

    @Override
    public void readMeta(PacketInputStream in) {

    }
}
