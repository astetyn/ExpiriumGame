package com.astetyne.main.entity;

import com.astetyne.main.Constants;
import com.astetyne.main.Resources;
import com.astetyne.main.items.ItemType;
import com.astetyne.main.stages.RunningGameStage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class DroppedItemEntity extends Entity {

    private final ItemType type;
    private final SpriteBatch batch;

    public DroppedItemEntity(int id, ItemType type, RunningGameStage game, float angleVel, Vector2 loc) {
        super(id, 0.5f, 0.5f);
        this.type = type;
        this.batch = game.getBatch();
        setupBody(angleVel, loc, game);
    }

    @Override
    public void draw() {
        batch.draw(Resources.STONE_TEXTURE, getLocation().x - width/2, getLocation().y - height/2, width, height);
    }

    private void setupBody(float angleVel, Vector2 loc, RunningGameStage game) {

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(loc);
        bodyDef.angularVelocity = angleVel;

        body = game.getGameWorld().getB2dWorld().createBody(bodyDef);

        PolygonShape polyShape = new PolygonShape();
        polyShape.setAsBox(0.25f, 0.25f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 30f;
        fixtureDef.restitution = 0.2f;
        fixtureDef.friction = 0.2f;
        fixtureDef.shape = polyShape;
        fixtureDef.filter.categoryBits = Constants.DEFAULT_BIT;

        body.createFixture(fixtureDef);
        polyShape.dispose();

        getTargetPosition().set(loc);

        game.getGameWorld().getEntitiesID().put(ID, this);
        game.getGameWorld().getEntities().add(this);
    }
}
