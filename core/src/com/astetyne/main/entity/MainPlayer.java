package com.astetyne.main.entity;

import com.astetyne.main.gui.ThumbStick;
import com.astetyne.main.net.client.actions.PlayerMoveActionC;
import com.astetyne.main.net.netobjects.SVector;
import com.astetyne.main.stages.GameStage;
import com.astetyne.main.utils.Constants;
import com.astetyne.main.world.input.TileBreaker;
import com.astetyne.main.world.input.TilePlacer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class MainPlayer extends Entity {

    private final MainPlayerAnimator animator;
    private final TileBreaker tileBreaker;
    private final TilePlacer tilePlacer;
    private final ThumbStick movementTS;

    public MainPlayer(int id, Vector2 loc) {
        super(id, 0.9f, 1.25f);
        setupBody(loc);
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

    private void setupBody(Vector2 loc) {

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(loc);

        body = GameStage.get().getWorld().getB2dWorld().createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 30f;
        fixtureDef.restitution = 0f;
        fixtureDef.filter.categoryBits = Constants.PLAYER_BIT;
        fixtureDef.filter.maskBits = Constants.DEFAULT_BIT;

        body.setFixedRotation(true);

        // upper poly
        PolygonShape polyShape = new PolygonShape();
        polyShape.setAsBox(0.45f, 0.55f, new Vector2(0.45f, 0.7f), 0);
        fixtureDef.shape = polyShape;
        fixtureDef.friction = 0;
        body.createFixture(fixtureDef);

        // bottom poly
        float[] verts = new float[8];
        verts[0] = 0.05f;
        verts[1] = 0.15f;
        verts[2] = 0.2f;
        verts[3] = 0;
        verts[4] = 0.7f;
        verts[5] = 0;
        verts[6] = 0.85f;
        verts[7] = 0.15f;

        polyShape.set(verts);
        fixtureDef.shape = polyShape;
        fixtureDef.friction = 1;
        body.createFixture(fixtureDef);

        // sensor
        polyShape.setAsBox(0.4f, 0.3f, new Vector2(0.45f, 0.2f), 0);
        fixtureDef.density = 0f;
        fixtureDef.shape = polyShape;
        fixtureDef.isSensor = true;
        Fixture jumpSensor = body.createFixture(fixtureDef);

        polyShape.dispose();

        GameStage.get().getWorld().getCL().registerListener(jumpSensor, this);
        GameStage.get().getWorld().getEntitiesID().put(ID, this);

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

    public PlayerMoveActionC generateMoveAction() {
        return new PlayerMoveActionC(new SVector(getLocation()), new SVector(getVelocity()));
    }

}
