package com.astetyne.expirium.client.entity;

import com.astetyne.expirium.client.entity.animator.StaticEntityAnimator;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.badlogic.gdx.math.Vector2;

public class DroppedItemEntity extends Entity {

    private final Item dropItem;

    public DroppedItemEntity(int id, Vector2 loc, PacketInputStream in) {
        super(EntityType.DROPPED_ITEM, id, loc);
        dropItem = Item.getType(in.getInt());
        setAnimator(new StaticEntityAnimator(this, dropItem.getTexture()));
    }

}
