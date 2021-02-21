package com.astetyne.expirium.server.core.world.tile.meta;

import com.astetyne.expirium.client.tiles.Material;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.core.event.Source;
import com.astetyne.expirium.server.core.world.ExpiWorld;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.astetyne.expirium.server.core.world.tile.ExpiTile;
import com.astetyne.expirium.server.core.world.tile.MetaTile;

import java.io.DataInputStream;
import java.io.IOException;

public class MetaTileTorch extends MetaTile {

    private final static int duration = Consts.SERVER_TPS * 60 * 4;

    private final long placeTick;

    public MetaTileTorch(ExpiWorld world, ExpiTile owner) {
        super(world, owner);
        placeTick = System.currentTimeMillis();
        scheduleAfter(this::onEnd, duration);
    }

    public MetaTileTorch(ExpiWorld world, ExpiTile owner, DataInputStream in) throws IOException {
        super(world, owner);
        placeTick = in.readLong();
        long tickPassed = world.getTick() - placeTick;
        scheduleAfter(this::onEnd, duration - tickPassed);
    }

    private void onEnd() {
        world.changeMaterial(owner, Material.AIR, false, Source.NATURAL);
    }

    @Override
    public void writeData(WorldBuffer out) {
        out.writeLong(placeTick);
    }
}
