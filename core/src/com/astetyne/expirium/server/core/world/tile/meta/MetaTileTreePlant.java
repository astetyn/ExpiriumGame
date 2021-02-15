package com.astetyne.expirium.server.core.world.tile.meta;

import com.astetyne.expirium.client.tiles.Material;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.IntVector2;
import com.astetyne.expirium.client.utils.Utils;
import com.astetyne.expirium.server.core.event.Source;
import com.astetyne.expirium.server.core.world.ExpiWorld;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.astetyne.expirium.server.core.world.tile.ExpiTile;
import com.astetyne.expirium.server.core.world.tile.MetaTile;

import java.io.DataInputStream;
import java.io.IOException;

public class MetaTileTreePlant extends MetaTile {

    private final static int GROW_TIME = Consts.SERVER_TPS * 300;

    private long growTick;

    public MetaTileTreePlant(ExpiWorld world, ExpiTile owner) {
        super(world, owner);
        growTick = -1;
    }

    public MetaTileTreePlant(ExpiWorld world, ExpiTile owner, DataInputStream in) throws IOException {
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
        if(owner.getMeta() != this) return;
        out.writeLong(growTick);
    }

    private void grow() {
        if(owner.getMeta() != this) return;

        // if soil is not grass or dirt
        if(owner.getY() == 0 || (world.getTileAt(owner.getLoc().add(0, -1)).getMaterial() != Material.GRASS &&
                world.getTileAt(owner.getLoc().add(0, -1)).getMaterial() != Material.DIRT)) {
            growTick = world.scheduleTask(this::grow, Utils.getRandAddTime(GROW_TIME));
            return;
        }

        if(owner.getMaterial() == Material.GROWING_PLANT_SHOREA && growShorea(owner.getLoc())) return;
        else if(owner.getMaterial() == Material.GROWING_PLANT_FIR && growFir(owner.getLoc())) return;

        // in case of unsuccessful grow
        growTick = world.scheduleTask(this::grow, Utils.getRandAddTime(GROW_TIME));
    }

    private boolean growShorea(IntVector2 loc) {

        int x = loc.x;
        int y = loc.y;

        if(x == 0 || x == world.getTerrainWidth()-1) return false;

        int treeHeight = Math.min((int) (Math.random() * 5) + 5, y + world.getTerrainHeight()-1);

        ExpiTile[][] terrain = world.getTerrain();

        // air check
        for(int i = 1; i < Math.min(3, treeHeight); i++) {
            if(terrain[y + i][x].getMaterial() != Material.AIR) return false;
        }

        double[] randomVals = new double[treeHeight-3];

        for(int i = 3; i < treeHeight; i++) {
            double rand = Math.random();
            randomVals[i-3] = rand;
            if(rand < 0.6) {
                if(terrain[y + i][x].getMaterial() != Material.AIR) return false;
            }else if(rand < 0.8) {
                if(terrain[y + i][x].getMaterial() != Material.AIR
                        || terrain[y + i][x+1].getMaterial() != Material.AIR) return false;
            }else {
                if(terrain[y + i][x].getMaterial() != Material.AIR
                        || terrain[y + i][x-1].getMaterial() != Material.AIR) return false;
            }
        }
        if(terrain[y + treeHeight][x].getMaterial() != Material.AIR) return false;

        // confirmed
        for(int i = 0; i < Math.min(3, treeHeight); i++) {
            world.changeMaterial(terrain[y + i][x], Material.LOG_SHOREA, false, Source.NATURAL);
        }

        for(int i = 3; i < treeHeight; i++) {
            double rand = randomVals[i-3];
            if(rand < 0.6) {
                world.changeMaterial(terrain[y + i][x], Material.LOG_SHOREA, false, Source.NATURAL);
            }else if(rand < 0.8) {
                world.changeMaterial(terrain[y + i][x], Material.LOG_SHOREA_RIGHT, false, Source.NATURAL);
                world.changeMaterial(terrain[y + i][x + 1], Material.LEAVES_SHOREA_RIGHT, false, Source.NATURAL);
            }else {
                world.changeMaterial(terrain[y + i][x], Material.LOG_SHOREA_LEFT, false, Source.NATURAL);
                world.changeMaterial(terrain[y + i][x - 1], Material.LEAVES_SHOREA_LEFT, false, Source.NATURAL);
            }
        }
        world.changeMaterial(terrain[y + treeHeight][x], Material.LEAVES_SHOREA_TOP, false, Source.NATURAL);
        return true;
    }

    private boolean growFir(IntVector2 loc) {

        int x = loc.x;
        int y = loc.y;

        if(x == 0 || x == world.getTerrainWidth()-1) return false;

        ExpiTile[][] terrain = world.getTerrain();

        int treeHeight = (int) (Math.random() * 5) + 5;

        if(y + treeHeight >= world.getTerrainHeight()-1) return false;

        for(int i = 1; i < treeHeight; i++) {
            if(terrain[y + i][x].getMaterial() != Material.AIR) return false;
            if(terrain[y + i][x+1].getMaterial() != Material.AIR) return false;
            if(terrain[y + i][x-1].getMaterial() != Material.AIR) return false;
        }
        if(terrain[y + treeHeight][x].getMaterial() != Material.AIR) return false;

        // confirmed

        world.changeMaterial(terrain[y][x], Material.LOG_FIR, false, Source.NATURAL);

        for(int i = 1; i < treeHeight; i++) {
            world.changeMaterial(terrain[y + i][x], Material.LEAVES_FIR_FULL, false, Source.NATURAL);
            world.changeMaterial(terrain[y + i][x+1], Material.LEAVES_FIR_RIGHT, false, Source.NATURAL);
            world.changeMaterial(terrain[y + i][x-1], Material.LEAVES_FIR_LEFT, false, Source.NATURAL);
        }
        world.changeMaterial(terrain[y + treeHeight][x], Material.LEAVES_FIR_TOP, false, Source.NATURAL);

        return true;
    }
}
