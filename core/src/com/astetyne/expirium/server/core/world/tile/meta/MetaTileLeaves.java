package com.astetyne.expirium.server.core.world.tile.meta;

import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.core.world.ExpiWorld;
import com.astetyne.expirium.server.core.world.tile.ExpiTile;
import com.astetyne.expirium.server.core.world.tile.MetaTile;

public class MetaTileLeaves extends MetaTile {

    public MetaTileLeaves(ExpiWorld world, ExpiTile owner) {
        super(world, owner);
    }

    @Override
    public void postInit() {
        world.scheduleTask(this::plantSapling, Consts.SERVER_TPS * (int)(Math.random()*1200+600));
    }

    public void plantSapling() {
        if(owner.getMeta() != this) return;
        world.scheduleTask(this::plantSapling, Consts.SERVER_TPS * (int)(Math.random()*1200+600));
        //todo: najst vhodne miesto na zasadenie, changnut na sapling
    }

    @Override
    public void dropItems() {
        if(Math.random() < 0.3) {
            dropItem(Item.APPLE);
        }
    }

}