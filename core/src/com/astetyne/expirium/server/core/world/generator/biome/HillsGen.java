package com.astetyne.expirium.server.core.world.generator.biome;

import com.astetyne.expirium.server.core.world.generator.Noise;
import com.astetyne.expirium.server.core.world.tile.Material;

public class HillsGen extends BiomeGenerator {

    public HillsGen(Material[][] terrain, int[] surface, int w, int h, long seed) {
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
                if(y <= terrainHeight && y > terrainHeight-2) {
                    terrain[x][y] = Material.RHYOLITE;
                }else if(y < terrainHeight) {
                    terrain[x][y] = Material.LIMESTONE;
                }else {
                    terrain[x][y] = Material.AIR;
                }
            }
        }
        createRandSurfacePlacements(from, to, Material.BLUEBERRY_BUSH_GROWN, 0.08, 2);
        createOreSpots(Material.CHROMITE, 0.2, from, to);
    }

    @Override
    public int getH(int x) {
        return (int) (50 + Noise.noise(x / 16f, seed) * 20);
    }
}
