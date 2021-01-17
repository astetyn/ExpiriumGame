package com.astetyne.expirium.server.api.world.generator;

import com.astetyne.expirium.client.tiles.TileType;
import com.astetyne.expirium.server.api.world.tiles.ExpiTile;

public class WorldGenerator {

    private final ExpiTile[][] worldTerrain;
    private final int w, h;
    private final int[] terrainLevel;

    public WorldGenerator(ExpiTile[][] worldTerrain) {
        this.worldTerrain = worldTerrain;
        this.h = worldTerrain.length;
        this.w = worldTerrain[0].length;
        terrainLevel = new int[w];
    }

    public void generateWorld() {

        for(int x = 0; x < w; x++) {

            int grassHeight = (int) (50 + Noise.noise((x) / 16.0f, 0, 0) * 20);
            terrainLevel[x] = grassHeight;

            for(int y = 0; y < h; y++) {
                if(y == grassHeight) {
                    worldTerrain[y][x] = new ExpiTile(TileType.GRASS, TileType.AIR, x, y);
                }else if(y < grassHeight && y > grassHeight-7) {
                    worldTerrain[y][x] = new ExpiTile(TileType.DIRT, TileType.AIR, x, y);
                }else if(y < grassHeight) {
                    worldTerrain[y][x] = new ExpiTile(TileType.STONE, TileType.AIR, x, y);
                }else {
                    worldTerrain[y][x] = new ExpiTile(TileType.AIR, TileType.AIR, x, y);
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
                    worldTerrain[terrainLevel[x - 1]][x-1].setTypeFront(TileType.GRASS_SLOPE_L);
                }else if(grassHeight > terrainLevel[x - 1]) {
                    worldTerrain[grassHeight][x].setTypeFront(TileType.GRASS_SLOPE_R);
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
                    worldTerrain[bottom + i][left + j].setTypeFront(TileType.RHYOLITE);
                }
            }
            lastHill = x;
        }
    }

    private void generateTrees() {

        int lastTree = 0;

        for(int x = 0; x < w; x++) {

            int y = terrainLevel[x] + 1;

            if(worldTerrain[y-1][x].getTypeFront() != TileType.GRASS) continue;

            if(Math.random() < 0.8f || lastTree + 2 > x) continue;

            int treeHeight = Math.max((int) (Math.random() * 10), 5);

            // 3 tiles tall trunk fixed
            for(int i = 0; i < 3; i++) {
                worldTerrain[y + i][x].setTypeFront(TileType.TREE1);
            }

            // trunk random
            for(int i = 3; i < treeHeight; i++) {

                double rand = Math.random();

                if(rand < 0.6) {
                    worldTerrain[y + i][x].setTypeFront(TileType.TREE1);
                }else if(rand < 0.8) {
                    worldTerrain[y + i][x].setTypeFront(TileType.TREE2);
                    if(x != w - 1) worldTerrain[y + i][x + 1].setTypeFront(TileType.TREE6);
                }else {
                    worldTerrain[y + i][x].setTypeFront(TileType.TREE3);
                    worldTerrain[y + i][x - 1].setTypeFront(TileType.TREE5);
                }
            }
            worldTerrain[y + treeHeight][x].setTypeFront(TileType.TREE4);

            lastTree = x;
        }
    }

    private void generateBushes() {

        int lastRasp = 0;

        for(int x = 0; x < w; x++) {

            int y = terrainLevel[x] + 1;

            if(worldTerrain[y - 1][x].getTypeFront() != TileType.GRASS || worldTerrain[y][x].getTypeFront() != TileType.AIR) continue;

            if(Math.random() < 0.8f || lastRasp + 2 > x) continue;

            worldTerrain[y][x].setTypeFront(TileType.RASPBERRY_BUSH_2);
        }

    }

}
