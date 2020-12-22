package com.astetyne.expirium.main.entity;

import com.astetyne.expirium.main.items.Item;
import com.astetyne.expirium.main.items.ItemType;
import com.astetyne.expirium.main.stages.GameStage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.nio.ByteBuffer;

public class DroppedItemEntity extends Entity {

    private final SpriteBatch batch;
    private Item dropItem;

    public DroppedItemEntity(int id, Vector2 loc, ByteBuffer bb) {
        super(EntityType.DROPPED_ITEM, id, loc, 0.5f, 0.5f);
        this.batch = GameStage.get().getBatch();
        readMeta(bb);
    }

    @Override
    public void draw() {
        batch.draw(dropItem.getTexture(), getLocation().x - width/2, getLocation().y - height/2, width/2, height/2, width, height, 1, 1, (float) (body.getAngle()*180/Math.PI));
    }

    @Override
    public void readMeta(ByteBuffer bb) {
        dropItem = ItemType.getType(bb.getInt()).initItem();
    }

}
