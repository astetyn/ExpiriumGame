package com.astetyne.main.world;

import com.astetyne.main.Constants;
import com.astetyne.main.net.netobjects.SWorldChunk;

public class WorldChunk {

    private final int id;
    private final Tile[][] terrain;

    public WorldChunk(SWorldChunk chunk) {
        this.id = chunk.getId();

        terrain = new Tile[Constants.TILES_HEIGHT_CHUNK][Constants.TILES_WIDTH_CHUNK];

        for(int i = 0; i < Constants.TILES_HEIGHT_CHUNK; i++) {
            for(int j = 0; j < Constants.TILES_WIDTH_CHUNK; j++) {
                terrain[i][j] = chunk.getTerrain()[i][j].createInstance();
            }
        }
    }

    public int getId() {
        return id;
    }

    public Tile[][] getTerrain() {
        return terrain;
    }
}
