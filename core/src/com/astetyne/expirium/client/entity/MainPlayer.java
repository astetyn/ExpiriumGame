package com.astetyne.expirium.client.entity;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.screens.GameScreen;
import com.astetyne.expirium.client.world.input.TilePlacer;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class MainPlayer extends Entity {

    private final PlayerEntityAnimator animator;
    private final TilePlacer tilePlacer;

    public MainPlayer(int id, Vector2 loc) {
        super(EntityType.PLAYER, id, loc, 0.9f, 1.25f);
        tilePlacer = new TilePlacer();
        GameScreen.get().getMultiplexer().addProcessor(tilePlacer);
        animator = new PlayerEntityAnimator(ExpiGame.get().getBatch(), this);
    }

    public void draw(SpriteBatch batch) {
        animator.draw();
    }

    @Override
    public void readMeta(PacketInputStream in) {

    }

    public TilePlacer getTilePlacer() {
        return tilePlacer;
    }
}
