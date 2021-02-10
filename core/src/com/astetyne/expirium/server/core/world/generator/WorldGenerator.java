package com.astetyne.expirium.server.core.world.generator;

import com.astetyne.expirium.client.tiles.Material;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;

public class WorldGenerator {

    private final Material[][] terrain;
    private final int w, h;
    private final int[] terrainLevel;
    private final long seed;

    public WorldGenerator(int width, int height, long seed) {
        w = width;
        h = height;
        this.seed = seed;
        terrain = new Material[h][w];
        terrainLevel = new int[w];
    }

    public void generateWorld() {

        boolean sandZone = false;
        int sandZoneStart = -1;
        int sandZoneLen = 0;
        int lastSandHeight = 0;

        for(int x = 0; x < w; x++) {

            int terrainHeight = (int) (50 + Noise.noise((x) / 16.0f, 0, 0, seed) * 20);

            if(!sandZone && Math.random() < 0.01) {
                sandZone = true;
                sandZoneStart = x;
                sandZoneLen = (int) (Math.random() * 20) + 10;
                lastSandHeight = terrainHeight;
            }

            if(sandZoneStart + sandZoneLen < x) {
                sandZone = false;
            }

            if(sandZone) terrainHeight = lastSandHeight;

            terrainLevel[x] = terrainHeight;

            for(int y = 0; y < h; y++) {
                if(y == terrainHeight) {
                    if(sandZone) {
                        terrain[y][x] = Material.SAND;
                    }else {
                        terrain[y][x] = Material.GRASS;
                    }
                }else if(y < terrainHeight && y > terrainHeight-7) {
                    if(sandZone) {
                        terrain[y][x] = Material.SAND;
                    }else {
                        terrain[y][x] = Material.DIRT;
                    }
                }else if(y < terrainHeight) {
                    terrain[y][x] = Material.STONE;
                }else {
                    terrain[y][x] = Material.AIR;
                }
            }
        }
        makeSlopes();
        makeRhyoliteHills();
        generateTrees();
        generateBushes();

    }

    private void makeSlopes() {
        for(int x = 0; x < w; x++) {
            int grassHeight = terrainLevel[x];

            if(x != 0) {
                if(grassHeight < terrainLevel[x - 1]) {
                    terrain[terrainLevel[x - 1]][x-1] = Material.GRASS_SLOPE_L;
                }else if(grassHeight > terrainLevel[x - 1]) {
                    terrain[grassHeight][x] = Material.GRASS_SLOPE_R;
                }
            }
        }
    }

    private void makeRhyoliteHills() {

        int lastHill = 0;

        for(int x = 0; x < w; x++) {

            if(Math.random() < 0.6f || lastHill + 20 > x) continue;

            int bottom = Math.max(terrainLevel[x] - 2, 0);

            for(int i = 0; i < 2; i++) { // y-levels

                int width = (int) (Math.min(Math.random() * 3 + 2, w - x));

                int left = Math.max(x - width, 0);
                for(int j = 0; j < width; j++) {
                    if(bottom + i > terrainLevel[left+j] + 1) continue;
                    terrain[bottom + i][left + j] = Material.RHYOLITE;
                }
            }
            lastHill = x;
        }
    }

    private void generateTrees() {

        int lastTree = 0;

        for(int x = 0; x < w; x++) {

            int y = terrainLevel[x] + 1;

            if(terrain[y-1][x] != Material.GRASS) continue;

            if(Math.random() < 0.8f || lastTree + 2 > x) continue;

            int treeHeight = Math.max((int) (Math.random() * 10), 5);

            // 3 tiles tall trunk fixed
            for(int i = 0; i < 3; i++) {
                terrain[y + i][x] = Material.LOG1;
            }

            // trunk random
            for(int i = 3; i < treeHeight; i++) {

                double rand = Math.random();

                if(rand < 0.6) {
                    terrain[y + i][x] = Material.LOG1;
                }else if(rand < 0.8) {
                    terrain[y + i][x] = Material.LOG2;
                    if(x != w - 1) terrain[y + i][x + 1] = Material.LEAVES3;
                }else {
                    terrain[y + i][x] = Material.LOG3;
                    terrain[y + i][x - 1] = Material.LEAVES2;
                }
            }
            terrain[y + treeHeight][x] = Material.LEAVES1;

            lastTree = x;
        }
    }

    private void generateBushes() {

        int lastRasp = 0;

        for(int x = 0; x < w; x++) {

            int y = terrainLevel[x] + 1;

            if(terrain[y - 1][x] != Material.GRASS || terrain[y][x] != Material.AIR) continue;

            if(Math.random() < 0.9f || lastRasp + 2 > x) continue;

            terrain[y][x] = Material.RASPBERRY_BUSH_2;
            lastRasp = x;
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
