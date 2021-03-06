package com.astetyne.expirium.server.core.world.tile.meta;

import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.IntVector2;
import com.astetyne.expirium.client.utils.Utils;
import com.astetyne.expirium.server.core.event.Source;
import com.astetyne.expirium.server.core.world.World;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.astetyne.expirium.server.core.world.tile.Material;
import com.astetyne.expirium.server.core.world.tile.MetaTile;
import com.astetyne.expirium.server.core.world.tile.Tile;

import java.io.DataInputStream;
import java.io.IOException;

public class MetaTileCactus extends MetaTile {

    private final static int GROW_TIME = Consts.SERVER_TPS * 300;

    private long growTick;

    public MetaTileCactus(World world, Tile owner) {
        super(world, owner);
        growTick = scheduleAfter(this::grow, Utils.getRandAddTime(GROW_TIME));
    }

    public MetaTileCactus(World world, Tile owner, DataInputStream in) throws IOException {
        super(world, owner);
        growTick = in.readLong();
        schedule(this::grow, growTick);
    }

    @Override
    public void writeData(WorldBuffer out) {
        out.writeLong(growTick);
    }

    private void grow() {

        int cactusHeight = (int) (Math.random() * 4) + 1;

        if(owner.getY() + cactusHeight >= world.getTerrainHeight()-1) return;

        // air check
        for(int i = 1; i < cactusHeight+1; i++) {
            Tile t = world.getTileAt(owner.getX(), owner.getY() + i);
            if(t.getMaterial() != Material.AIR) {
                growTick = scheduleAfter(this::grow, Utils.getRandAddTime(GROW_TIME/2));
                return;
            }
        }

        for(int i = 0; i < cactusHeight; i++) {
            Tile t = world.getTileAt(owner.getX(), owner.getY() + i);
            world.changeMaterial(t, getRandCactus(), false, Source.NATURAL);
        }
        IntVector2 vec = new IntVector2(0, 0);
        world.changeMaterial(world.getTileAt(owner.getLoc(vec).add(0, cactusHeight)), Material.CACTUS_TOP, false, Source.NATURAL);
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
