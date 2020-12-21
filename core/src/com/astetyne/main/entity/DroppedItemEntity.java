package com.astetyne.main.entity;

import com.astetyne.main.items.Item;
import com.astetyne.main.stages.GameStage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class DroppedItemEntity extends Entity {

    private final Item item;
    private final SpriteBatch batch;

    public DroppedItemEntity(int id, Item item, float angleVel, Vector2 loc) {
        super(EntityType.DROPPED_ITEM, id, loc, 0.5f, 0.5f);
        body.setAngularVelocity(angleVel);
        this.item = item;
        this.batch = GameStage.get().getBatch();
    }

    @Override
    public void draw() {
        batch.draw(item.getTexture(), getLocation().x - width/2, getLocation().y - height/2, width/2, height/2, width, height, 1, 1, (float) (body.getAngle()*180/Math.PI));
    }

}
