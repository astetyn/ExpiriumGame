package com.astetyne.expirium.server.core.world.generator.biome;

import com.astetyne.expirium.server.core.world.generator.Noise;
import com.astetyne.expirium.server.core.world.tile.Material;

public class TropicalForestGen extends BiomeGenerator {

    public TropicalForestGen(Material[][] terrain, int[] surface, int w, int h, long seed) {
        super(terrain, surface, w, h, seed);
    }

    @Override
    public void generate(int i, int leftMH, int rightMH) {
        super.generate(i, leftMH, rightMH);
        int from = i*100;
        int to = (i+1)*100;

        for(int x = from; x < to; x++) {
            int terrainHeight = surface[x];

            for(int y = 0; y < h; y++) {
                if(y == terrainHeight) {
                    terrain[x][y] = Material.GRASS;
                }else if(y < terrainHeight && y > terrainHeight-10) {
                    terrain[x][y] = Material.DIRT;
                }else if(y < terrainHeight) {
                    terrain[x][y] = Material.LIMESTONE;
                }else {
                    terrain[x][y] = Material.AIR;
                }
            }

            if(x != from) {
                if(terrainHeight < surface[x - 1]) {
                    terrain[x-1][surface[x - 1]] = Material.GRASS_SLOPE_L;
                }else if(terrainHeight > surface[x - 1]) {
                    terrain[x][terrainHeight] = Material.GRASS_SLOPE_R;
                }
            }
        }
        createShoreaTrees(from, to);
        createRandSurfacePlacements(from, to, Material.RASPBERRY_BUSH_GROWN, 0.08, 2);
        createOreSpots(Material.COAL_ORE, 0.2, from, to);
    }

    @Override
    public int getH(int x) {
        return (int) (50 + Noise.noise(x / 16.0f, seed) * 10);
    }

    private void createShoreaTrees(int from, int to) {

        int last = from;

        for(int x = from+1; x < to-1; x++) {

            int y = surface[x] + 1;

            if(terrain[x][y-1].getSolidity().isLabile() || last + 3 >= x || Math.random() > 0.5) continue;

            int treeHeight = (int) (Math.random() * 5) + 5;

            if(y + treeHeight >= h) continue;

            last = x;

            // 3 tiles tall trunk fixed
            for(int i = 0; i < 3; i++) {
                terrain[x][y + i] = Material.LOG_SHOREA;
            }

            // trunk random
            for(int i = 3; i < treeHeight; i++) {

                double rand = Math.random();

                if(rand < 0.6) {
                    terrain[x][y + i] = Material.LOG_SHOREA;
                }else if(rand < 0.8) {
                    terrain[x][y + i] = Material.LOG_SHOREA_RIGHT;
                    if(x != w - 1) terrain[x + 1][y + i] = Material.LEAVES_SHOREA_RIGHT;
                }else {
                    terrain[x][y + i] = Material.LOG_SHOREA_LEFT;
                    terrain[x - 1][y + i] = Material.LEAVES_SHOREA_LEFT;
                }
            }
            terrain[x][y + treeHeight] = Material.LEAVES_SHOREA_TOP;
        }
    }
}
