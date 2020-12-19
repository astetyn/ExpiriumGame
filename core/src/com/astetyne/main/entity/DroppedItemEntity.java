package com.astetyne.main.entity;

import com.astetyne.main.items.Item;
import com.astetyne.main.stages.GameStage;
import com.astetyne.main.utils.Constants;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class DroppedItemEntity extends Entity {

    private final Item item;
    private final SpriteBatch batch;

    public DroppedItemEntity(int id, Item item, float angleVel, Vector2 loc) {
        super(id, 0.5f, 0.5f);
        this.item = item;
        this.batch = GameStage.get().getBatch();
        setupBody(angleVel, loc);
    }

    @Override
    public void draw() {
        batch.draw(item.getTexture(), getLocation().x - width/2, getLocation().y - height/2, width/2, height/2, width, height, 1, 1, (float) (body.getAngle()*180/Math.PI));
    }

    private void setupBody(float angleVel, Vector2 loc) {

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(loc);
        bodyDef.angularVelocity = angleVel;

        body = GameStage.get().getWorld().getB2dWorld().createBody(bodyDef);

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

        GameStage.get().getWorld().getEntitiesID().put(ID, this);
        GameStage.get().getWorld().getEntities().add(this);
    }
}
