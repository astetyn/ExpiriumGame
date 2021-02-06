package com.astetyne.expirium.server.core.world.tile.meta;

import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.tiles.Solidity;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.world.tile.ExpiTile;
import com.astetyne.expirium.server.core.world.tile.MetaTile;
import com.astetyne.expirium.server.core.world.tile.TileFix;

public class MetaTileCoalOre extends MetaTile {

    public MetaTileCoalOre(ExpiServer server, ExpiTile owner) {
        super(server, owner);
    }

    @Override
    public void dropItems() {
        dropItem(Item.COAL);
    }

    @Override
    public Solidity getSolidity() {
        return Solidity.SOLID;
    }

    @Override
    public TileFix getFix() {
        return TileFix.FULL;
    }

    @Override
    public int getMaxStability() {
        return 3;
    }

    @Override
    public float getBreakTime() {
        return 10;
    }
}
