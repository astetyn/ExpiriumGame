package com.astetyne.expirium.server.core.world.tile.meta;

import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.core.world.World;
import com.astetyne.expirium.server.core.world.tile.MetaTile;
import com.astetyne.expirium.server.core.world.tile.Tile;

public class MetaTileLeaves extends MetaTile {

    public MetaTileLeaves(World world, Tile owner) {
        super(world, owner);
        scheduleAfter(this::plantSapling, Consts.SERVER_TPS * (int)(Math.random()*1200+600));
    }

    public void plantSapling() {
        scheduleAfter(this::plantSapling, Consts.SERVER_TPS * (int)(Math.random()*1200+600));
        //todo: najst vhodne miesto na zasadenie, changnut na sapling
    }

    @Override
    public void dropItems() {
        switch(owner.getMaterial()) {
            case LEAVES_FIR_FULL:
            case LEAVES_FIR_TOP:
                dropItem(Item.RAW_WOOD);
                break;
            case LEAVES_FIR_LEFT:
            case LEAVES_FIR_RIGHT:
                if(Math.random() < 0.3) {
                    dropItem(Item.FIR_CONE);
                }
                break;
            case LEAVES_SHOREA_LEFT:
            case LEAVES_SHOREA_RIGHT:
            case LEAVES_SHOREA_TOP:
                if(Math.random() < 0.3) {
                    dropItem(Item.APPLE);
                }
                break;
        }
    }

}
