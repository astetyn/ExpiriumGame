package com.astetyne.main.entity;

import com.astetyne.main.gui.ThumbStick;
import com.astetyne.main.net.client.packets.PlayerMovePacket;
import com.astetyne.main.stages.GameStage;
import com.astetyne.main.world.input.TileBreaker;
import com.astetyne.main.world.input.TilePlacer;
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

    public PlayerMovePacket generateMoveAction() {
        return new PlayerMovePacket(getLocation(), getVelocity());
    }

}
