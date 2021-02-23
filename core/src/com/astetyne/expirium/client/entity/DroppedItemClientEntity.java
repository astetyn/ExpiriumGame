package com.astetyne.expirium.client.entity;

import com.astetyne.expirium.client.entity.animator.StaticEntityAnimator;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.world.ClientWorld;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.badlogic.gdx.math.Vector2;

public class DroppedItemClientEntity extends ClientEntity {

    private final Item dropItem;

    public DroppedItemClientEntity(ClientWorld world, short id, Vector2 loc, PacketInputStream in) {
        super(world, EntityType.DROPPED_ITEM, id, loc);
        dropItem = Item.getType(in.getInt());
        setAnimator(new StaticEntityAnimator(this, dropItem.getTexture()));
    }

}
