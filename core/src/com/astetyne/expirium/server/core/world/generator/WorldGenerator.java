package com.astetyne.expirium.server.core.world.generator;

import com.astetyne.expirium.client.tiles.Material;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.world.tile.ExpiTile;

public class WorldGenerator {

    private final ExpiServer server;
    private final ExpiTile[][] worldTerrain;
    private final int w, h;
    private final int[] terrainLevel;
    private final long seed;

    public WorldGenerator(ExpiServer server, ExpiTile[][] worldTerrain, long seed) {
        this.server = server;
        this.worldTerrain = worldTerrain;
        this.seed = seed;
        this.h = worldTerrain.length;
        this.w = worldTerrain[0].length;
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
                        worldTerrain[y][x] = new ExpiTile(server, Material.SAND, x, y);
                    }else {
                        worldTerrain[y][x] = new ExpiTile(server, Material.GRASS, x, y);
                    }
                }else if(y < terrainHeight && y > terrainHeight-7) {
                    if(sandZone) {
                        worldTerrain[y][x] = new ExpiTile(server, Material.SAND, x, y);
                    }else {
                        worldTerrain[y][x] = new ExpiTile(server, Material.DIRT, x, y);
                    }
                }else if(y < terrainHeight) {
                    worldTerrain[y][x] = new ExpiTile(server, Material.STONE, x, y);
                }else {
                    worldTerrain[y][x] = new ExpiTile(server, Material.AIR, x, y);
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
                    worldTerrain[terrainLevel[x - 1]][x-1].setMaterial(Material.GRASS_SLOPE_L);
                }else if(grassHeight > terrainLevel[x - 1]) {
                    worldTerrain[grassHeight][x].setMaterial(Material.GRASS_SLOPE_R);
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
                    worldTerrain[bottom + i][left + j].setMaterial(Material.RHYOLITE);
                }
            }
            lastHill = x;
        }
    }

    private void generateTrees() {

        int lastTree = 0;

        for(int x = 0; x < w; x++) {

            int y = terrainLevel[x] + 1;

            if(worldTerrain[y-1][x].getMaterial() != Material.GRASS) continue;

            if(Math.random() < 0.8f || lastTree + 2 > x) continue;

            int treeHeight = Math.max((int) (Math.random() * 10), 5);

            // 3 tiles tall trunk fixed
            for(int i = 0; i < 3; i++) {
                worldTerrain[y + i][x].setMaterial(Material.LOG1);
            }

            // trunk random
            for(int i = 3; i < treeHeight; i++) {

                double rand = Math.random();

                if(rand < 0.6) {
                    worldTerrain[y + i][x].setMaterial(Material.LOG1);
                }else if(rand < 0.8) {
                    worldTerrain[y + i][x].setMaterial(Material.LOG2);
                    if(x != w - 1) worldTerrain[y + i][x + 1].setMaterial(Material.LEAVES3);
                }else {
                    worldTerrain[y + i][x].setMaterial(Material.LOG3);
                    worldTerrain[y + i][x - 1].setMaterial(Material.LEAVES2);
                }
            }
            worldTerrain[y + treeHeight][x].setMaterial(Material.LEAVES1);

            lastTree = x;
        }
    }

    private void generateBushes() {

        int lastRasp = 0;

        for(int x = 0; x < w; x++) {

            int y = terrainLevel[x] + 1;

            if(worldTerrain[y - 1][x].getMaterial() != Material.GRASS || worldTerrain[y][x].getMaterial() != Material.AIR) continue;

            if(Math.random() < 0.9f || lastRasp + 2 > x) continue;

            worldTerrain[y][x].setMaterial(Material.RASPBERRY_BUSH_2);
            lastRasp = x;
        }

    }

}
