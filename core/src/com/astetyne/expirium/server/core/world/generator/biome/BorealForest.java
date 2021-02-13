package com.astetyne.expirium.server.core.world.generator.biome;

import com.astetyne.expirium.client.tiles.Material;
import com.astetyne.expirium.server.core.world.generator.Noise;

public class BorealForest extends Biome {

    public BorealForest(Material[][] terrain, int[] surface, int w, int h, long seed) {
        super(terrain, surface, w, h, seed);
    }

    @Override
    public void generate(int from, int to, int leftMH, int rightMH) {
        super.generate(from, to, leftMH, rightMH);
        for(int x = from; x < to; x++) {
            int terrainHeight = surface[x];

            //System.out.println("x: "+x+" diff: "+(leftMH - getH(x))+" xDiff: "+(x-from)+" smooth: "+(smoothing - (x-from))/smoothing * (leftMH - getH(x)));

            for(int y = 0; y < h; y++) {
                if(y == terrainHeight) {
                    terrain[y][x] = Material.GRASS;
                }else if(y < terrainHeight && y > terrainHeight-10) {
                    terrain[y][x] = Material.DIRT;
                }else if(y < terrainHeight) {
                    terrain[y][x] = Material.STONE;
                }else {
                    terrain[y][x] = Material.AIR;
                }
            }

            if(x != from) {
                if(terrainHeight < surface[x - 1]) {
                    terrain[surface[x - 1]][x-1] = Material.GRASS_SLOPE_L;
                }else if(terrainHeight > surface[x - 1]) {
                    terrain[terrainHeight][x] = Material.GRASS_SLOPE_R;
                }
            }
        }
        //createFirTrees(from, to);
        //generateBlueberryBushes(from, to);
    }

    @Override
    public int getH(int x) {
        return (int) (50 + Noise.noise(x / 16.0f, seed) * 10);
    }
}
