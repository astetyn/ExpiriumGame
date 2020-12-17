package com.astetyne.main.entity;

import com.astetyne.main.Constants;
import com.astetyne.main.stages.RunningGameStage;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class PlayerEntity extends Entity {

    private final PlayerEntityAnimator animator;

    public PlayerEntity(int id, Vector2 loc,  RunningGameStage game) {
        super(id, 0.9f, 1.25f);
        animator = new PlayerEntityAnimator(game.getBatch(), this);
        setupBody(loc, game);
    }

    @Override
    public void draw() {
        animator.draw();
    }

    private void setupBody(Vector2 loc, RunningGameStage game) {

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(loc);

        body = game.getGameWorld().getB2dWorld().createBody(bodyDef);

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

        game.getGameWorld().getCL().registerListener(jumpSensor, this);
        game.getGameWorld().getEntitiesID().put(ID, this);
        game.getGameWorld().getEntities().add(this);

    }
}
