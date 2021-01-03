package com.astetyne.expirium.main.world;

import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.main.world.tiles.Tile;
import com.astetyne.expirium.main.world.tiles.TileType;
import com.astetyne.expirium.server.backend.PacketInputStream;

public class WorldChunk {

    private final int id;
    private final Tile[][] terrain;

    public WorldChunk(PacketInputStream in) {

        id = in.getInt();

        terrain = new Tile[Consts.T_H_CH][Consts.T_W_CH];

        for(int i = 0; i < Consts.T_H_CH; i++) {
            for(int j = 0; j < Consts.T_W_CH; j++) {
                TileType type = TileType.getType(in.getByte());
                int stability = in.getByte();
                terrain[i][j] = new Tile(id, j, i, stability, type);
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
