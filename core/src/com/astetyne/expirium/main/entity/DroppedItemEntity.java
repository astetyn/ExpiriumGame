package com.astetyne.expirium.main.entity;

import com.astetyne.expirium.main.items.Item;
import com.astetyne.expirium.main.stages.GameStage;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class DroppedItemEntity extends Entity {

    private final SpriteBatch batch;
    private Item dropItem;

    public DroppedItemEntity(int id, Vector2 loc, PacketInputStream in) {
        super(EntityType.DROPPED_ITEM, id, loc, 0.5f, 0.5f);
        this.batch = GameStage.get().getBatch();
        readMeta(in);
    }

    @Override
    public void draw() {
        batch.draw(dropItem.getItemTexture(), getLocation().x - width/2, getLocation().y - height/2, width/2, height/2, width, height, 1, 1, (float) (body.getAngle()*180/Math.PI));
    }

    @Override
    public void readMeta(PacketInputStream in) {
        dropItem = Item.getType(in.getInt());
    }

}
