package com.astetyne.expirium.server.core.world.generator.biome;

import com.astetyne.expirium.client.tiles.Material;
import com.astetyne.expirium.server.core.world.generator.Noise;

public class Desert extends Biome {

    public Desert(Material[][] terrain, int[] surface, int w, int h, long seed) {
        super(terrain, surface, w, h, seed);
    }

    @Override
    public void generate(int from, int to, int leftMH, int rightMH) {
        super.generate(from, to, leftMH, rightMH);

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

            if(x != 0) {
                if(terrainHeight < surface[x - 1]) {
                    terrain[surface[x - 1]][x-1] = Material.SAND_SLOPE_LEFT;
                }else if(terrainHeight > surface[x - 1]) {
                    terrain[terrainHeight][x] = Material.SAND_SLOPE_RIGHT;
                }
            }
        }
        //generateCacti(from, to);
    }

    @Override
    public int getH(int x) {
        return (int) (50 + Noise.noise(x / 32.0f, seed) * 4);
    }
}
