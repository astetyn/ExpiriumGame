package com.astetyne.expirium.server.core.world.tile.meta;

import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.tiles.Solidity;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.world.tile.ExpiTile;
import com.astetyne.expirium.server.core.world.tile.MetaTile;
import com.astetyne.expirium.server.core.world.tile.TileFix;

public class MetaTileCampfire extends MetaTile {

    public MetaTileCampfire(ExpiServer server, ExpiTile owner) {
        super(server, owner);
    }

    @Override
    public void dropItems() {
        dropItem(Item.CAMPFIRE);
    }

    @Override
    public Solidity getSolidity() {
        return Solidity.LABILE_VERT;
    }

    @Override
    public TileFix getFix() {
        return TileFix.CAMPFIRE;
    }

    @Override
    public int getMaxStability() {
        return 1;
    }

    @Override
    public float getBreakTime() {
        return 0.5f;
    }
}
