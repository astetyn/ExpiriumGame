package com.astetyne.expirium.server.core.world.generator.biome;

import com.astetyne.expirium.client.tiles.Material;
import com.astetyne.expirium.server.core.world.generator.Noise;

public class Desert extends BiomeGenerator {

    public Desert(Material[][] terrain, int[] surface, int w, int h, long seed) {
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
                if(y <= terrainHeight && y > terrainHeight-10) {
                    terrain[y][x] = Material.SAND;
                }else if(y < terrainHeight) {
                    terrain[y][x] = Material.STONE;
                }else {
                    terrain[y][x] = Material.AIR;
                }
            }

            if(x != from) {
                if(terrainHeight < surface[x - 1]) {
                    terrain[surface[x - 1]][x-1] = Material.SAND_SLOPE_LEFT;
                }else if(terrainHeight > surface[x - 1]) {
                    terrain[terrainHeight][x] = Material.SAND_SLOPE_RIGHT;
                }
            }
        }
        generateCacti(from, to);
        createRandSurfacePlacements(from, to, Material.LIMESTONE, Math.random()*0.1, 1);
        createCoalOres(from, to);
    }

    @Override
    public int getH(int x) {
        return (int) (50 + Noise.noise(x / 32.0f, seed) * 4);
    }

    private void generateCacti(int from, int to) {

        int last = from;

        for(int x = from; x < to; x++) {

            int y = surface[x] + 1;

            if(terrain[y-1][x].getSolidity().isLabile() || last + 10 > x || Math.random() < 0.8) continue;

            last = x;

            int cactusHeight = (int) (Math.random()*3) + 3;
            for(int i = 0; i < cactusHeight; i++) {

                double rand = Math.random();

                if(rand < 0.33) {
                    terrain[y + i][x] = Material.CACTUS_DOUBLE;
                }else if(rand < 0.66) {
                    terrain[y + i][x] = Material.CACTUS_LEFT;
                }else {
                    terrain[y + i][x] = Material.CACTUS_RIGHT;
                }
            }
            terrain[y+cactusHeight-1][x] = Material.CACTUS_TOP;
        }
    }
}
