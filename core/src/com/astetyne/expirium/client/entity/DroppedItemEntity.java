package com.astetyne.expirium.client.entity;

import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class DroppedItemEntity extends Entity {

    private Item dropItem;

    public DroppedItemEntity(int id, Vector2 loc, PacketInputStream in) {
        super(EntityType.DROPPED_ITEM, id, loc, 0.5f, 0.5f);
        readMeta(in);
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(dropItem.getTexture(), getLocation().x - width/2, getLocation().y - height/2, width/2, height/2, width, height, 1, 1, (float) (angle*180/Math.PI));
    }

    @Override
    public void readMeta(PacketInputStream in) {
        dropItem = Item.getType(in.getInt());
    }

}
