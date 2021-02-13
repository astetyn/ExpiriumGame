package com.astetyne.expirium.server.core.world.generator;

import com.astetyne.expirium.client.tiles.Material;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.astetyne.expirium.server.core.world.generator.biome.BorealForest;
import com.astetyne.expirium.server.core.world.generator.biome.TropicalForest;

public class WorldGenerator {

    private final Material[][] terrain;
    private final int w, h;
    private final int[] surface;
    private final long seed;

    public WorldGenerator(int width, int height, long seed) {
        w = width;
        h = height;
        this.seed = seed;
        terrain = new Material[h][w];
        surface = new int[w];
    }

    public void generateWorld() {

        TropicalForest tropicalForest = new TropicalForest(terrain, surface, w, h, seed);
        BorealForest borealForest = new BorealForest(terrain, surface, w, h, seed);
        tropicalForest.generate(0, w/2, 40, 40);
        borealForest.generate(w/2, w, 40, 40);
        //generateTropicalForest(0, w/2);
        //generateDesert(w/2, w);

    }

    private void generateDesert(int from, int to) {

        for(int x = from; x < to; x++) {
            int terrainHeight = (int) (50 + Noise.noise(x / 32.0f, seed) * 4);
            surface[x] = terrainHeight;

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
        generateCacti(from, to);
    }

    private void generateHills(int from, int to) {

    }

    private void makeRhyoliteHills() {

        int lastHill = 0;

        for(int x = 0; x < w; x++) {

            if(Math.random() < 0.6f || lastHill + 20 > x) continue;

            int bottom = Math.max(surface[x] - 2, 0);

            for(int i = 0; i < 2; i++) { // y-levels

                int width = (int) (Math.min(Math.random() * 3 + 2, w - x));

                int left = Math.max(x - width, 0);
                for(int j = 0; j < width; j++) {
                    if(bottom + i > surface[left+j] + 1) continue;
                    terrain[bottom + i][left + j] = Material.RHYOLITE;
                }
            }
            lastHill = x;
        }
    }

    private void createShoreaTrees(int from, int to) {

        int last = from;

        for(int x = from+1; x < to-1; x++) {

            int y = surface[x] + 1;

            if(terrain[y-1][x].getSolidity().isLabile() || last + 3 >= x || Math.random() > 0.5) continue;

            int treeHeight = (int) (Math.random() * 5) + 5;

            if(y + treeHeight >= h) continue;

            last = x;

            // 3 tiles tall trunk fixed
            for(int i = 0; i < 3; i++) {
                terrain[y + i][x] = Material.LOG_SHOREA;
            }

            // trunk random
            for(int i = 3; i < treeHeight; i++) {

                double rand = Math.random();

                if(rand < 0.6) {
                    terrain[y + i][x] = Material.LOG_SHOREA;
                }else if(rand < 0.8) {
                    terrain[y + i][x] = Material.LOG_SHOREA_RIGHT;
                    if(x != w - 1) terrain[y + i][x + 1] = Material.LEAVES_SHOREA_RIGHT;
                }else {
                    terrain[y + i][x] = Material.LOG_SHOREA_LEFT;
                    terrain[y + i][x - 1] = Material.LEAVES_SHOREA_LEFT;
                }
            }
            terrain[y + treeHeight][x] = Material.LEAVES_SHOREA_TOP;
        }
    }

    private void createFirTrees(int from, int to) {

        int last = from;

        for(int x = from+1; x < to-1; x++) {

            int y = surface[x] + 1;

            if(terrain[y-1][x].getSolidity().isLabile() || last + 3 >= x || Math.random() > 0.5) continue;

            int treeHeight = (int) (Math.random() * 5) + 5;

            if(y + treeHeight >= h) continue;

            last = x;

            // 3 tiles tall trunk fixed
            for(int i = 0; i < 1; i++) {
                terrain[y + i][x] = Material.LOG_FIR;
            }

            for(int i = 1; i < treeHeight; i++) {
                terrain[y + i][x] = Material.LEAVES_FIR_FULL;
                terrain[y + i][x+1] = Material.LEAVES_FIR_RIGHT;
                terrain[y + i][x-1] = Material.LEAVES_FIR_LEFT;
            }
            terrain[y + treeHeight][x] = Material.LEAVES_FIR_TOP;
        }
        generateBlueberryBushes(from, to);
    }

    private void generateCacti(int from, int to) {

        int last = from;

        for(int x = from+1; x < to-1; x++) {

            int y = surface[x] + 1;

            if(terrain[y-1][x].getSolidity().isLabile() || last + 10 > x || Math.random() < 0.8) continue;

            last = x;

            int cactusHeight = (int) (Math.random()*3) + 3;
            for(int i = 0; i < cactusHeight; i++) {

                double rand = Math.random();

                if(rand < 0.33) {
                    terrain[y + i][x] = Material.CACTUS_DOUBLE;
                }else if(rand < 0.66) {
                    terrain[y + i][x] = Material.CACTUS_LEFT;
                }else {
                    terrain[y + i][x] = Material.CACTUS_RIGHT;
                }
            }
            terrain[y+cactusHeight-1][x] = Material.CACTUS_TOP;
        }
    }

    private void generateRaspberryBushes(int from, int to) {

        int last = 0;

        for(int x = from+1; x < to-1; x++) {
            int y = surface[x] + 1;

            if(terrain[y - 1][x].getSolidity().isLabile() || terrain[y][x] != Material.AIR ||
                    Math.random() < 0.95f || last + 2 > x) continue;

            terrain[y][x] = Material.RASPBERRY_BUSH_GROWN;
            last = x;
        }
    }

    private void generateBlueberryBushes(int from, int to) {

        int last = 0;

        for(int x = from+1; x < to-1; x++) {
            int y = surface[x] + 1;

            if(terrain[y - 1][x].getSolidity().isLabile() || terrain[y][x] != Material.AIR ||
                    Math.random() < 0.95f || last + 2 > x) continue;

            terrain[y][x] = Material.BLUEBERRY_BUSH_GROWN;
            last = x;
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
