package com.astetyne.expirium.server.core.world.tile.meta;

import com.astetyne.expirium.client.tiles.Material;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.Utils;
import com.astetyne.expirium.server.core.event.Source;
import com.astetyne.expirium.server.core.world.ExpiWorld;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.astetyne.expirium.server.core.world.tile.ExpiTile;
import com.astetyne.expirium.server.core.world.tile.MetaTile;

import java.io.DataInputStream;
import java.io.IOException;

public class MetaTileCactus extends MetaTile {

    private final static int GROW_TIME = Consts.SERVER_TPS * 300;

    private long growTick;

    public MetaTileCactus(ExpiWorld world, ExpiTile owner) {
        super(world, owner);
        growTick = -1;
    }

    public MetaTileCactus(ExpiWorld world, ExpiTile owner, DataInputStream in) throws IOException {
        super(world, owner);
        growTick = in.readLong();
    }

    @Override
    public void postInit() {
        if(growTick == -1) {
            growTick = world.scheduleTask(this::grow, Utils.getRandAddTime(GROW_TIME));
        }else {
            world.scheduleTaskOn(this::grow, growTick);
        }
    }

    @Override
    public void writeData(WorldBuffer out) {
        out.writeLong(growTick);
    }

    private void grow() {
        if(owner.getMeta() != this) return;

        int cactusHeight = (int) (Math.random() * 4) + 1;

        if(owner.getY() + cactusHeight >= world.getTerrainHeight()-1) return;

        // air check
        for(int i = 1; i < cactusHeight+1; i++) {
            ExpiTile t = world.getTileAt(owner.getX(), owner.getY() + i);
            if(t.getMaterial() != Material.AIR) {
                growTick = world.scheduleTask(this::grow, Utils.getRandAddTime(GROW_TIME/2));
                return;
            }
        }

        for(int i = 0; i < cactusHeight; i++) {
            ExpiTile t = world.getTileAt(owner.getX(), owner.getY() + i);
            world.changeMaterial(t, getRandCactus(), false, Source.NATURAL);
        }
        world.changeMaterial(world.getTileAt(owner.getLoc().add(0, cactusHeight)), Material.CACTUS_TOP, false, Source.NATURAL);
    }

    private Material getRandCactus() {
        double d = Math.random();
        if(d < 0.33) {
            return Material.CACTUS_RIGHT;
        }else if(d < 0.66) {
            return Material.CACTUS_LEFT;
        }else if(d < 1) {
            return Material.CACTUS_DOUBLE;
        }
        return Material.CACTUS_DOUBLE;
    }
}
