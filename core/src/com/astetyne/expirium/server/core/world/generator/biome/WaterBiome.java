package com.astetyne.expirium.server.core.world.generator.biome;

import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.core.world.calculator.WaterEngine;
import com.astetyne.expirium.server.core.world.generator.Noise;
import com.astetyne.expirium.server.core.world.generator.WorldGenerator;
import com.astetyne.expirium.server.core.world.tile.Material;

public class WaterBiome extends BiomeGenerator {

    public WaterBiome(WorldGenerator gen) {
        super(gen);
    }

    @Override
    public void generate(int i, int leftMH, int rightMH) {
        super.generate(i, leftMH, rightMH);
        int from = i * Consts.WORLD_BIOME_WIDTH;
        int to = (i+1)*Consts.WORLD_BIOME_WIDTH;

        for(int x = from; x < to; x++) {
            int terrainHeight = surface[x];

            for(int y = 0; y < h; y++) {
                if(y <= terrainHeight && y > terrainHeight-10) {
                    terrain[x][y] = Material.SAND;
                }else if(y < terrainHeight) {
                    terrain[x][y] = Material.LIMESTONE;
                }else {
                    terrain[x][y] = Material.AIR;
                }
            }

            if(x != from) {
                if(terrainHeight < surface[x - 1]) {
                    terrain[x-1][surface[x - 1]] = Material.SAND_SLOPE_LEFT;
                }else if(terrainHeight > surface[x - 1]) {
                    terrain[x][terrainHeight] = Material.SAND_SLOPE_RIGHT;
                }
            }
        }
        createOreSpots(Material.COAL_ORE, 0.1, from, to);
        createOreSpots(Material.MAGNETITE, 0.3, from, to);

        int eighth = (to - from)/8;
        int startWater = from+eighth;
        int endWater = to-eighth;
        for(int x = startWater; x < endWater; x++) {
            int bottom = (int) (surface[x] - Math.sin((float)(x-startWater) * Math.PI / (endWater - startWater))*30);
            for(int y = bottom; y <= surface[x]; y++) {
                terrain[x][y] = Material.AIR;
                waterMask[x][y] = WaterEngine.maxLevel;
            }
        }
    }

    @Override
    public int getH(int x) {
        return (int) (50 + Noise.noise(x / 32.0f, seed) * 4);
    }
}
