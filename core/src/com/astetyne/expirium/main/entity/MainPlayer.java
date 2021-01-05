package com.astetyne.expirium.main.entity;

import com.astetyne.expirium.main.ExpiGame;
import com.astetyne.expirium.main.gui.widget.ThumbStick;
import com.astetyne.expirium.main.screens.GameScreen;
import com.astetyne.expirium.main.world.input.TileBreaker;
import com.astetyne.expirium.main.world.input.TilePlacer;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.badlogic.gdx.math.Vector2;

public class MainPlayer extends Entity {

    private final MainPlayerAnimator animator;
    private final TileBreaker tileBreaker;
    private final TilePlacer tilePlacer;
    private final ThumbStick movementTS;

    public MainPlayer(int id, Vector2 loc) {
        super(EntityType.PLAYER, id, loc, 0.9f, 1.25f);
        tileBreaker = new TileBreaker();
        tilePlacer = new TilePlacer();
        GameScreen.get().getMultiplexer().addProcessor(tilePlacer);
        movementTS = GameScreen.get().getGameStage().moveTS;
        animator = new MainPlayerAnimator(ExpiGame.get().getBatch(), this, movementTS);
    }

    public void draw() {
        animator.draw();
        tileBreaker.render();
    }

    public void update() {
        tileBreaker.update();
    }

    public void generateMovePacket() {
        float vert = movementTS.getVert();
        float horz = movementTS.getHorz();
        ExpiGame.get().getClientGateway().getManager().putTS1Packet(horz, vert);
    }

    @Override
    public void readMeta(PacketInputStream in) {

    }

    public TileBreaker getTileBreaker() {
        return tileBreaker;
    }

    public TilePlacer getTilePlacer() {
        return tilePlacer;
    }

    public ThumbStick getMovementTS() {
        return movementTS;
    }
}
