package com.astetyne.expirium.server.core.world.generator.biome;

import com.astetyne.expirium.client.tiles.Material;
import com.astetyne.expirium.client.utils.IntVector2;

public abstract class BiomeGenerator {

    protected static final float smoothing = 7;

    protected final Material[][] terrain;
    protected final int[] surface;
    protected final int w, h;
    protected final long seed;

    public BiomeGenerator(Material[][] terrain, int[] surface, int w, int h, long seed) {
        this.terrain = terrain;
        this.surface = surface;
        this.w = w;
        this.h = h;
        this.seed = seed;
    }

    // MH = midheight, target y for generation
    public void generate(int from, int to, int leftMH, int rightMH){
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

            if(terrain[y - 1][x].getSolidity().isLabile() || terrain[y][x] != Material.AIR ||
                    Math.random() > chance || last + minGap > x) continue;

            terrain[y][x] = mat;
            last = x;
        }
    }

    protected void createOreLayers(IntVector2 locMid, Material ore) {
        if(locMid.y == 0) return;
        int width1 = (int)(Math.random() * 3) + 1;
        int width2 = width1 + (int)(Math.random() * 4);
        if(locMid.x - width1/2 < 0 || locMid.x - width2/2 < 0) return;
        for(int i = 0; i < width1; i++) {
            if(terrain[locMid.y][locMid.x-width1/2+i] == Material.AIR) return;
        }
        for(int i = 0; i < width2; i++) {
            if(terrain[locMid.y-1][locMid.x-width2/2+i] == Material.AIR) return;
        }
        for(int i = 0; i < width1; i++) {
            terrain[locMid.y][locMid.x-width1/2+i] = ore;
        }
        for(int i = 0; i < width2; i++) {
            terrain[locMid.y-1][locMid.x-width2/2+i] = ore;
        }
    }

    protected void createCoalOres(int from, int to) {
        for(int x = from; x < to; x++) {
            if(Math.random() > 0.3) continue;
            createOreLayers(new IntVector2(x, (int) (Math.random() * (surface[x]-10))), Material.COAL_ORE);
        }
    }

    protected void createRhyoliteOres(int from, int to) {
        for(int x = from; x < to; x++) {
            if(Math.random() > 0.2) continue;
            createOreLayers(new IntVector2(x, (int) (Math.random() * (surface[x]-10))), Material.RHYOLITE);
        }
    }

}
