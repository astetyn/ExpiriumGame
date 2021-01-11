package com.astetyne.expirium.main.entity;

import com.astetyne.expirium.main.ExpiGame;
import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.gui.widget.ThumbStick;
import com.astetyne.expirium.main.screens.GameScreen;
import com.astetyne.expirium.main.world.input.TilePlacer;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.badlogic.gdx.math.Vector2;

public class MainPlayer extends Entity {

    private final MainPlayerAnimator animator;
    private final TilePlacer tilePlacer;
    private final ThumbStick movementTS, breakTS;

    public MainPlayer(int id, Vector2 loc) {
        super(EntityType.PLAYER, id, loc, 0.9f, 1.25f);
        tilePlacer = new TilePlacer();
        GameScreen.get().getMultiplexer().addProcessor(tilePlacer);
        movementTS = new ThumbStick(Res.THUMB_STICK_STYLE);
        breakTS = new ThumbStick(Res.THUMB_STICK_STYLE);
        animator = new MainPlayerAnimator(ExpiGame.get().getBatch(), this, movementTS);
    }

    public void draw() {
        animator.draw();
    }

    public void sendTSPacket() {
        ExpiGame.get().getClientGateway().getManager().putTSPacket(movementTS, breakTS);
    }

    @Override
    public void readMeta(PacketInputStream in) {

    }

    public TilePlacer getTilePlacer() {
        return tilePlacer;
    }
}
