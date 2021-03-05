package com.astetyne.expirium.server.core.world.tile.meta;

import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.server.core.world.World;
import com.astetyne.expirium.server.core.world.tile.MetaTile;
import com.astetyne.expirium.server.core.world.tile.Tile;

public class MetaTileNaturalMix extends MetaTile {

    public MetaTileNaturalMix(World world, Tile owner) {
        super(world, owner);
    }

    @Override
    public void dropItems() {
        dropItem(getRandDropItem());
    }

    private Item getRandDropItem() {
        int i = (int) (Math.random()*5);
        switch(i) {
            case 0: return Item.CLAYSTONE;
            case 1: return Item.RHYOLITE;
            case 2: return Item.LIMESTONE;
            case 3: return Item.FIR_CONE;
            case 4: return Item.COAL;
        }
        return Item.APPLE;
    }
}
