package com.astetyne.expirium.server.api.world;

import com.astetyne.expirium.main.world.tiles.TileType;

public class WorldGenerator {

    private final ExpiTile[][] worldTerrain;
    private final int w, h;

    public WorldGenerator(ExpiTile[][] worldTerrain) {
        this.worldTerrain = worldTerrain;
        this.h = worldTerrain.length;
        this.w = worldTerrain[0].length;
    }

    public void generateWorld() {

        for(int j = 0; j < w; j++) {

            int hh = (int) (0 + Noise.noise((j) / 32.0f, 0, 0) * 20);

            for(int i = 0; i < h; i++) {
                if(i == hh) {
                    if(Math.random() > 0.8) {
                        worldTerrain[i][j] = new ExpiTile(TileType.TREE1, j, i);
                    }else {
                        worldTerrain[i][j] = new ExpiTile(TileType.GRASS, j, i);
                    }
                }else if(i < hh && i > hh-5) {
                    worldTerrain[i][j] = new ExpiTile(TileType.DIRT, j, i);
                }else if(i < hh) {
                    worldTerrain[i][j] = new ExpiTile(TileType.STONE, j, i);
                }else {
                    worldTerrain[i][j] = new ExpiTile(TileType.AIR, j, i);
                }
            }

        }
    }

}
