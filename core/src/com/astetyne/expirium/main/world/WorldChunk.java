package com.astetyne.expirium.main.world;

import com.astetyne.expirium.main.utils.Constants;
import com.astetyne.expirium.main.world.tiles.Tile;
import com.astetyne.expirium.main.world.tiles.TileType;
import com.astetyne.expirium.server.backend.PacketInputStream;

public class WorldChunk {

    private final int id;
    private final Tile[][] terrain;

    public WorldChunk(PacketInputStream in) {

        id = in.getInt();

        terrain = new Tile[Constants.T_H_CH][Constants.T_W_CH];

        for(int i = 0; i < Constants.T_H_CH; i++) {
            for(int j = 0; j < Constants.T_W_CH; j++) {
                int type = in.getByte();
                int stability = in.getByte();
                terrain[i][j] = new Tile(this, j, i, TileType.getType(type), stability);
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
