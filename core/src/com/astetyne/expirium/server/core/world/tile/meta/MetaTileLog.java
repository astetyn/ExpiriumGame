package com.astetyne.expirium.server.core.world.tile.meta;

import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.tiles.Solidity;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.world.tile.ExpiTile;
import com.astetyne.expirium.server.core.world.tile.MetaTile;
import com.astetyne.expirium.server.core.world.tile.TileFix;

public class MetaTileLog extends MetaTile {

    public MetaTileLog(ExpiServer server, ExpiTile owner) {
        super(server, owner);
    }

    @Override
    public void dropItems() {
        dropItem(Item.RAW_WOOD);
    }

    @Override
    public Solidity getSolidity() {
        return Solidity.SOLID_VERT;
    }

    @Override
    public TileFix getFix() {
        return TileFix.SOFT;
    }

    @Override
    public int getMaxStability() {
        return 1;
    }

    @Override
    public float getBreakTime() {
        return 3;
    }

}
