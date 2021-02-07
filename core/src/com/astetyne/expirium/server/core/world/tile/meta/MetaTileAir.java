package com.astetyne.expirium.server.core.world.tile.meta;

import com.astetyne.expirium.client.tiles.Solidity;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.world.tile.ExpiTile;
import com.astetyne.expirium.server.core.world.tile.MetaTile;
import com.astetyne.expirium.server.core.world.tile.TileFix;

public class MetaTileAir extends MetaTile {

    public MetaTileAir(ExpiServer server, ExpiTile owner) {
        super(server, owner);
    }

    @Override
    public Solidity getSolidity() {
        return Solidity.LABILE;
    }

    @Override
    public TileFix getFix() {
        return TileFix.SOFT;
    }

    @Override
    public int getMaxStability() {
        return 0;
    }

    @Override
    public float getBreakTime() {
        return 0;
    }

}
