package com.astetyne.expirium.server.core.world.generator.biome;

import com.astetyne.expirium.client.tiles.Material;
import com.astetyne.expirium.server.core.world.generator.Noise;

public class Hills extends BiomeGenerator {

    public Hills(Material[][] terrain, int[] surface, int w, int h, long seed) {
        super(terrain, surface, w, h, seed);
    }

    @Override
    public void generate(int from, int to, int leftMH, int rightMH) {
        super.generate(from, to, leftMH, rightMH);
        for(int x = from; x < to; x++) {
            int terrainHeight = surface[x];

            for(int y = 0; y < h; y++) {
                if(y <= terrainHeight && y > terrainHeight-2) {
                    terrain[y][x] = Material.RHYOLITE;
                }else if(y < terrainHeight) {
                    terrain[y][x] = Material.STONE;
                }else {
                    terrain[y][x] = Material.AIR;
                }
            }
        }
        createRandSurfacePlacements(from, to, Material.BLUEBERRY_BUSH_GROWN, 0.08, 2);
    }

    @Override
    public int getH(int x) {
        return (int) (50 + Noise.noise(x / 16f, seed) * 20);
    }
}
