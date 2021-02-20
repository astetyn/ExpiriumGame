package com.astetyne.expirium.server.core.world.generator.biome;

import com.astetyne.expirium.client.tiles.Material;
import com.astetyne.expirium.server.core.world.generator.Noise;

public class BorealForestGen extends BiomeGenerator {

    public BorealForestGen(Material[][] terrain, int[] surface, int w, int h, long seed) {
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
                    terrain[y][x] = Material.GRASS;
                }else if(y < terrainHeight && y > terrainHeight-10) {
                    terrain[y][x] = Material.DIRT;
                }else if(y < terrainHeight) {
                    terrain[y][x] = Material.LIMESTONE;
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
        createFirTrees(from, to);
        createRandSurfacePlacements(from, to, Material.BLUEBERRY_BUSH_GROWN, 0.08, 2);
        createRandSurfacePlacements(from, to, Material.CLAYSTONE, 0.1, 1);
        createOreSpots(Material.RHYOLITE, 0.2, from, to);
        createOreSpots(Material.MAGNETITE, 0.2, from, to);
    }

    @Override
    public int getH(int x) {
        return (int) (50 + Noise.noise(x / 16.0f, seed) * 10);
    }

    private void createFirTrees(int from, int to) {

        int last = from;

        for(int x = from+1; x < to-1; x++) {

            int y = surface[x] + 1;

            if(terrain[y-1][x].getSolidity().isLabile() || last + 3 >= x || Math.random() > 0.5) continue;

            int treeHeight = (int) (Math.random() * 5) + 5;

            if(y + treeHeight >= h) continue;

            last = x;

            terrain[y][x] = Material.LOG_FIR;

            for(int i = 1; i < treeHeight; i++) {
                terrain[y + i][x] = Material.LEAVES_FIR_FULL;
                terrain[y + i][x+1] = Material.LEAVES_FIR_RIGHT;
                terrain[y + i][x-1] = Material.LEAVES_FIR_LEFT;
            }
            terrain[y + treeHeight][x] = Material.LEAVES_FIR_TOP;
        }
    }
}
