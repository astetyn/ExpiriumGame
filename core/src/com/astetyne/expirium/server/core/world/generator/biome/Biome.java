package com.astetyne.expirium.server.core.world.generator.biome;

import com.astetyne.expirium.client.tiles.Material;

public abstract class Biome {

    protected static final float smoothing = 5;

    protected final Material[][] terrain;
    protected final int[] surface;
    protected final int w, h;
    protected final long seed;

    public Biome(Material[][] terrain, int[] surface, int w, int h, long seed) {
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

}
