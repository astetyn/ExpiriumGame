package com.astetyne.expirium.main.entity;

import com.astetyne.expirium.main.ExpiriumGame;
import com.astetyne.expirium.main.gui.ThumbStick;
import com.astetyne.expirium.main.stages.GameStage;
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
        GameStage.get().getWorld().getCL().registerListener(EntityBodyFactory.createSensor(body), this);
        tileBreaker = new TileBreaker();
        tilePlacer = new TilePlacer();
        GameStage.get().getMultiplexer().addProcessor(tilePlacer);
        movementTS = GameStage.get().getGameGUI().getMovementTS();
        animator = new MainPlayerAnimator(GameStage.get().getBatch(), this, movementTS);
    }

    public void draw() {
        tileBreaker.render();
        animator.draw();
    }

    public void update() {

        tileBreaker.update();

        // movement
        float vert = movementTS.getVert();
        float horz = movementTS.getHorz();

        Vector2 center = body.getWorldCenter();
        float jump = 0;
        if(onGround) {
            if(body.getLinearVelocity().y < 5 && vert >= 0.6f) {
                jump = 1;
            }
        }
        if((body.getLinearVelocity().x >= 3 && horz > 0) || (body.getLinearVelocity().x <= -3 && horz < 0)) {
            horz = 0;
        }
        body.applyLinearImpulse(0, 60*jump, center.x, center.y, true);
        body.applyForceToCenter(1000f * horz, 0, true);

    }

    public void generateMovePacket() {
        ExpiriumGame.get().getClientGateway().getPacketManager().putPlayerMovePacket(getLocation(), getVelocity());
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
}
