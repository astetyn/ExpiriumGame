package com.astetyne.expirium.server.core.world.generator;

import com.astetyne.expirium.client.tiles.Material;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.astetyne.expirium.server.core.world.generator.biome.*;

public class WorldGenerator {

    private final Material[][] terrain;
    private final int w, h;
    private final int[] surface;
    private final long seed;
    private final TropicalForest tropicalForest;
    private final BorealForest borealForest;
    private final Desert desert;
    private final Hills hills;

    public WorldGenerator(int width, int height, long seed) {
        w = width;
        h = height;
        this.seed = seed;
        terrain = new Material[h][w];
        surface = new int[w];
        tropicalForest = new TropicalForest(terrain, surface, w, h, seed);
        borealForest = new BorealForest(terrain, surface, w, h, seed);
        desert = new Desert(terrain, surface, w, h, seed);
        hills = new Hills(terrain, surface, w, h, seed);
    }

    public void generateWorld() {

        int x = 0;
        BiomeGenerator lastBiome = null;
        BiomeGenerator nextBiome = getRandBiome();

        while(true) {
            int biomeWidth = (int) (30 + Math.random() * 30);
            int endX = Math.min(x+biomeWidth, w);

            BiomeGenerator actualBiome = nextBiome;
            nextBiome = getRandBiome();

            int leftMH = lastBiome == null ? 70 : (lastBiome.getH(x-1) + actualBiome.getH(x))/2;
            int rightMH = endX >= w ? 70 : (actualBiome.getH(endX) + nextBiome.getH(endX+1))/2;

            actualBiome.generate(x, endX, leftMH, rightMH);

            lastBiome = actualBiome;

            x += biomeWidth;
            if(x >= w) {
                break;
            }
        }

    }

    private BiomeGenerator getRandBiome() {
        double d = Math.random();
        if(d < 0.3) {
            return tropicalForest;
        }else if(d < 0.6) {
            return borealForest;
        }else if(d < 0.85) {
            return desert;
        }else {
            return hills;
        }
    }

    public void writeData(WorldBuffer wb) {
        for(int y = 0; y < h; y++) {
            for(int x = 0; x < w; x++) {
                Material mat = terrain[y][x];
                wb.writeMaterial(terrain[y][x]);
                mat.writeDefaultMetaData(wb);
                wb.writeBoolean(false);
            }
        }
    }
}
