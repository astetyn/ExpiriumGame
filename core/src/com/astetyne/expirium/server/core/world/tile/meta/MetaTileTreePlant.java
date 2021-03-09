package com.astetyne.expirium.server.core.world.tile.meta;

import com.astetyne.expirium.client.items.Item;
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

public class MetaTileTreePlant extends MetaTile {

    private final static int GROW_TIME = Consts.SERVER_TPS * 300;

    private long growTick;

    public MetaTileTreePlant(World world, Tile owner) {
        super(world, owner);
        growTick = scheduleAfter(this::grow, Utils.getRandAddTime(GROW_TIME));
    }

    public MetaTileTreePlant(World world, Tile owner, DataInputStream in) throws IOException {
        super(world, owner);
        growTick = in.readLong();
        schedule(this::grow, growTick);
    }

    @Override
    public void writeData(WorldBuffer out) {
        out.writeLong(growTick);
    }

    private void grow() {

        IntVector2 vec = new IntVector2(0, 0);

        // if soil is not grass or dirt
        if(world.getTileAt(owner.getLoc(vec).add(0, -1)).getMaterial() != Material.GRASS &&
                world.getTileAt(owner.getLoc(vec).add(0, -1)).getMaterial() != Material.DIRT) {
            world.changeMaterial(owner, Material.AIR, false, Source.NATURAL);
            dropItem(Item.DRY_LEAVES);
            return;
        }

        if(owner.getMaterial() == Material.GROWING_PLANT_SHOREA && growShorea(owner.getLoc(vec))) return;
        else if(owner.getMaterial() == Material.GROWING_PLANT_FIR && growFir(owner.getLoc(vec))) return;

        // in case of unsuccessful grow
        growTick = scheduleAfter(this::grow, Utils.getRandAddTime(GROW_TIME));
    }

    private boolean growShorea(IntVector2 loc) {

        int x = loc.x;
        int y = loc.y;

        if(x == 0 || x == world.getTerrainWidth()-1) return false;

        int treeHeight = Math.min((int) (Math.random() * 5) + 5, y + world.getTerrainHeight()-1);

        Tile[][] terrain = world.getTerrain();

        // air check
        for(int i = 1; i < Math.min(3, treeHeight); i++) {
            if(terrain[x][y + i].getMaterial() != Material.AIR) return false;
        }

        double[] randomVals = new double[treeHeight-3];

        for(int i = 3; i < treeHeight; i++) {
            double rand = Math.random();
            randomVals[i-3] = rand;
            if(rand < 0.6) {
                if(terrain[x][y + i].getMaterial() != Material.AIR) return false;
            }else if(rand < 0.8) {
                if(terrain[x][y + i].getMaterial() != Material.AIR
                        || terrain[x+1][y + i].getMaterial() != Material.AIR) return false;
            }else {
                if(terrain[x][y + i].getMaterial() != Material.AIR
                        || terrain[x-1][y + i].getMaterial() != Material.AIR) return false;
            }
        }
        if(terrain[x][y + treeHeight].getMaterial() != Material.AIR) return false;

        // confirmed
        for(int i = 0; i < Math.min(3, treeHeight); i++) {
            world.changeMaterial(terrain[x][y + i], Material.LOG_SHOREA, false, Source.NATURAL);
        }

        for(int i = 3; i < treeHeight; i++) {
            double rand = randomVals[i-3];
            if(rand < 0.6) {
                world.changeMaterial(terrain[x][y + i], Material.LOG_SHOREA, false, Source.NATURAL);
            }else if(rand < 0.8) {
                world.changeMaterial(terrain[x][y + i], Material.LOG_SHOREA_RIGHT, false, Source.NATURAL);
                world.changeMaterial(terrain[x+1][y + i], Material.LEAVES_SHOREA_RIGHT, false, Source.NATURAL);
            }else {
                world.changeMaterial(terrain[x][y + i], Material.LOG_SHOREA_LEFT, false, Source.NATURAL);
                world.changeMaterial(terrain[x-1][y + i], Material.LEAVES_SHOREA_LEFT, false, Source.NATURAL);
            }
        }
        world.changeMaterial(terrain[x][y + treeHeight], Material.LEAVES_SHOREA_TOP, false, Source.NATURAL);
        return true;
    }

    private boolean growFir(IntVector2 loc) {

        int x = loc.x;
        int y = loc.y;

        if(x == 0 || x == world.getTerrainWidth()-1) return false;

        Tile[][] terrain = world.getTerrain();

        int treeHeight = (int) (Math.random() * 5) + 5;

        if(y + treeHeight >= world.getTerrainHeight()-1) return false;

        for(int i = 1; i < treeHeight; i++) {
            if(terrain[x][y + i].getMaterial() != Material.AIR) return false;
            if(terrain[x+1][y + i].getMaterial() != Material.AIR) return false;
            if(terrain[x-1][y + i].getMaterial() != Material.AIR) return false;
        }
        if(terrain[x][y + treeHeight].getMaterial() != Material.AIR) return false;

        // confirmed

        world.changeMaterial(terrain[x][y], Material.LOG_FIR, false, Source.NATURAL);

        for(int i = 1; i < treeHeight; i++) {
            world.changeMaterial(terrain[x][y + i], Material.LEAVES_FIR_FULL, false, Source.NATURAL);
            world.changeMaterial(terrain[x+1][y + i], Material.LEAVES_FIR_RIGHT, false, Source.NATURAL);
            world.changeMaterial(terrain[x-1][y + i], Material.LEAVES_FIR_LEFT, false, Source.NATURAL);
        }
        world.changeMaterial(terrain[x][y + treeHeight], Material.LEAVES_FIR_TOP, false, Source.NATURAL);

        return true;
    }
}
