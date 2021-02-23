package com.astetyne.expirium.server.core.world.generator;

import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.astetyne.expirium.server.core.world.generator.biome.BiomeGenerator;
import com.astetyne.expirium.server.core.world.generator.biome.BiomeType;
import com.astetyne.expirium.server.core.world.tile.Material;

public class WorldGenerator {

    private final Material[][] terrain;
    private final int w, h;
    private final int[] surface;
    private final long seed;
    private final BiomeType[] biomes;

    public WorldGenerator(int width, int height, long seed) {
        w = width;
        h = height;
        this.seed = seed;
        terrain = new Material[w][h];
        surface = new int[w];
        biomes = new BiomeType[w/ Consts.BIOME_LEN];
    }

    public void generateWorld() {

        for(BiomeType type : BiomeType.values()) {
            while(true) {
                int randPos = (int) (Math.random() * biomes.length);
                if(biomes[randPos] != null) continue;
                biomes[randPos] = type;
                break;
            }
        }

        for(int i = 0; i < biomes.length; i++) {
            if(biomes[i] != null) continue;
            biomes[i] = getRandBiomeType();
        }

        int lastMH = h-2;

        for(int i = 0; i < biomes.length-1; i++) {

            BiomeGenerator currentGen = biomes[i].initGenerator(terrain, surface, w, h, seed);
            BiomeGenerator nextGen = biomes[i+1].initGenerator(terrain, surface, w, h, seed);

            int rightMH = (currentGen.getH((i+1)*Consts.BIOME_LEN-1) + nextGen.getH((i+1)*Consts.BIOME_LEN)) / 2;

            currentGen.generate(i, lastMH, rightMH);

            lastMH = rightMH;
        }

        BiomeGenerator lastGen = biomes[biomes.length-1].initGenerator(terrain, surface, w, h, seed);
        lastGen.generate(biomes.length-1, lastMH, h-2);
    }

    private BiomeType getRandBiomeType() {
        double d = Math.random();
        if(d < 0.3) {
            return BiomeType.TROPICAL_FOREST;
        }else if(d < 0.6) {
            return BiomeType.BOREAL_FOREST;
        }else if(d < 0.85) {
            return BiomeType.DESERT;
        }else {
            return BiomeType.HILLS;
        }
    }

    public void writeData(WorldBuffer wb) {
        for(int x = 0; x < w; x++) {
            for(int y = 0; y < h; y++) {
                wb.writeMaterial(terrain[x][y]);
                wb.writeByte((byte) 0); // waterLevel
                wb.writeBoolean(false); // backwall
            }
        }
        for(BiomeType biome : biomes) {
            wb.writeInt(biome.getId());
        }
    }
}
