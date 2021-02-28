package com.astetyne.expirium.server.core.world.generator.biome;

import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.IntVector2;
import com.astetyne.expirium.server.core.world.generator.WorldGenerator;
import com.astetyne.expirium.server.core.world.tile.Material;

public abstract class BiomeGenerator {

    protected static final float smoothing = 7;

    protected final Material[][] terrain;
    protected final byte[][] waterMask;
    protected final boolean[][] backWallMask;
    protected final int[] surface;
    protected final int w, h;
    protected final long seed;

    public BiomeGenerator(WorldGenerator gen) {
        this.terrain = gen.getTerrain();
        this.waterMask = gen.getWaterMask();
        backWallMask = gen.getBackWallMask();
        this.surface = gen.getSurface();
        this.w = gen.getW();
        this.h = gen.getH();
        this.seed = gen.getSeed();
    }

    // MH = midheight, target y for generation
    public void generate(int i, int leftMH, int rightMH){
        int from = i * Consts.WORLD_BIOME_WIDTH;
        int to = (i+1)*Consts.WORLD_BIOME_WIDTH;
        for(int x = from; x < to; x++) {
            surface[x] = getSmoothedH(x, from, to, leftMH, rightMH);
        }
    }

    public abstract int getH(int x); //without smoothing

    protected int getSmoothedH(int x, int from, int to, int leftMH, int rightMH) {
        int smooth = x-from <= smoothing ? (int) ((smoothing - (x - from)) / smoothing * (leftMH - getH(x))) : 0;
        smooth = to-x <= smoothing ? (int) ((smoothing - (to - x)) / smoothing * (rightMH - getH(x))) : smooth;
        return getH(x) + smooth;
    }

    protected void createRandSurfacePlacements(int from, int to, Material mat, double chance, int minGap) {

        int last = 0;

        for(int x = from; x < to; x++) {
            int y = surface[x] + 1;

            if(terrain[x][y - 1].getSolidity().isLabile() || terrain[x][y] != Material.AIR ||
                    Math.random() > chance || last + minGap > x) continue;

            terrain[x][y] = mat;
            last = x;
        }
    }

    protected void createOreSpot(IntVector2 locMid, Material ore) {
        if(locMid.y == 0) return;
        int width1 = (int)(Math.random() * 3) + 1;
        int width2 = width1 + (int)(Math.random() * 4);
        if(locMid.x - width1/2 < 0 || locMid.x - width2/2 < 0 || locMid.x + width1/2 >= w || locMid.x + width2/2 >= w) return;
        for(int i = 0; i < width1; i++) {
            if(terrain[locMid.x-width1/2+i][locMid.y] == Material.AIR) return;
        }
        for(int i = 0; i < width2; i++) {
            if(terrain[locMid.x-width2/2+i][locMid.y-1] == Material.AIR) return;
        }
        for(int i = 0; i < width1; i++) {
            terrain[locMid.x-width1/2+i][locMid.y] = ore;
        }
        for(int i = 0; i < width2; i++) {
            terrain[locMid.x-width2/2+i][locMid.y-1] = ore;
        }
    }

    protected void createOreSpots(Material mat, double density, int from, int to) {
        for(int x = from; x < to; x++) {
            if(Math.random() < density) continue;
            createOreSpot(new IntVector2(x, (int) (Math.random() * (surface[x]-10))), mat);
        }
    }

}
