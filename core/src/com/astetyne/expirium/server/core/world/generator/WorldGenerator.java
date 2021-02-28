package com.astetyne.expirium.server.core.world.generator;

import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.astetyne.expirium.server.core.world.generator.biome.BiomeGenerator;
import com.astetyne.expirium.server.core.world.generator.biome.BiomeType;
import com.astetyne.expirium.server.core.world.tile.Material;

public class WorldGenerator {

    private final Material[][] terrain;
    private final byte[][] waterMask;
    private final boolean[][] backWallMask;
    private final int w, h;
    private final int[] surface;
    private final long seed;
    private final BiomeType[] biomes;

    public WorldGenerator(int width, int height, long seed) {
        w = width;
        h = height;
        this.seed = seed;
        terrain = new Material[w][h];
        waterMask = new byte[w][h];
        for(int x = 0; x < w; x++) {
            for(int y = 0; y < h; y++) {
                waterMask[x][y] = 0;
            }
        }
        backWallMask = new boolean[w][h];
        surface = new int[w];
        biomes = new BiomeType[w / Consts.WORLD_BIOME_WIDTH];
    }

    public void generateWorld() {

        if(BiomeType.values().length > biomes.length) {
            throw new IllegalArgumentException();
        }

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

            BiomeGenerator currentGen = biomes[i].initGenerator(this);
            BiomeGenerator nextGen = biomes[i+1].initGenerator(this);

            int rightMH = (currentGen.getH((i+1)*Consts.WORLD_BIOME_WIDTH -1) + nextGen.getH((i+1)*Consts.WORLD_BIOME_WIDTH)) / 2;

            currentGen.generate(i, lastMH, rightMH);

            lastMH = rightMH;
        }

        BiomeGenerator lastGen = biomes[biomes.length-1].initGenerator(this);
        lastGen.generate(biomes.length-1, lastMH, h-2);
    }

    private BiomeType getRandBiomeType() {
        double d = Math.random();
        if(d < 0.2) {
            return BiomeType.TROPICAL_FOREST;
        }else if(d < 0.5) {
            return BiomeType.BOREAL_FOREST;
        }else if(d < 0.7) {
            return BiomeType.DESERT;
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
                wb.writeByte(waterMask[x][y]); // waterLevel
                wb.writeBoolean(false); // backwall
            }
        }
        for(BiomeType biome : biomes) {
            wb.writeInt(biome.ordinal());
        }
    }

    public Material[][] getTerrain() {
        return terrain;
    }

    public byte[][] getWaterMask() {
        return waterMask;
    }

    public boolean[][] getBackWallMask() {
        return backWallMask;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public int[] getSurface() {
        return surface;
    }

    public long getSeed() {
        return seed;
    }

    public BiomeType[] getBiomes() {
        return biomes;
    }
}
