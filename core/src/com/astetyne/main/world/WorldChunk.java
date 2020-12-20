package com.astetyne.main.world;

import com.astetyne.main.utils.Constants;
import com.astetyne.main.world.tiles.Tile;

import java.nio.ByteBuffer;

public class WorldChunk {

    private final int id;
    private final Tile[][] terrain;

    public WorldChunk(ByteBuffer bb) {

        this.id = bb.getInt();

        terrain = new Tile[Constants.T_H_CH][Constants.T_W_CH];

        for(int i = 0; i < Constants.T_H_CH; i++) {
            for(int j = 0; j < Constants.T_W_CH; j++) {
                terrain[i][j] = new Tile(this, j, i, TileType.getType(bb.getInt()));
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
