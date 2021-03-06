package com.astetyne.expirium.server.core.world.generator.biome;

import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.core.world.generator.Noise;
import com.astetyne.expirium.server.core.world.generator.WorldGenerator;
import com.astetyne.expirium.server.core.world.tile.Material;

public class BorealForestGen extends BiomeGenerator {

    public BorealForestGen(WorldGenerator gen) {
        super(gen);
    }

    @Override
    public void generate(int i, int leftMH, int rightMH) {
        super.generate(i, leftMH, rightMH);
        int from = i* Consts.WORLD_BIOME_WIDTH;
        int to = (i+1)*Consts.WORLD_BIOME_WIDTH;
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

            if(terrain[x][y-1].getSolidity().isLabile() || last + 3 >= x || Math.random() > 0.5) continue;

            int treeHeight = (int) (Math.random() * 5) + 5;

            if(y + treeHeight >= h) continue;

            last = x;

            terrain[x][y] = Material.LOG_FIR;

            for(int i = 1; i < treeHeight; i++) {
                terrain[x][y + i] = Material.LEAVES_FIR_FULL;
                terrain[x+1][y + i] = Material.LEAVES_FIR_RIGHT;
                terrain[x-1][y + i] = Material.LEAVES_FIR_LEFT;
            }
            terrain[x][y + treeHeight] = Material.LEAVES_FIR_TOP;
        }
    }
}
