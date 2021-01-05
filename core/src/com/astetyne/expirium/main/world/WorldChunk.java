package com.astetyne.expirium.main.world;

import com.astetyne.expirium.main.screens.GameScreen;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.main.world.tiles.Tile;
import com.astetyne.expirium.main.world.tiles.TileType;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class WorldChunk implements Pool.Poolable {

    private int id;
    private final Tile[][] terrain;

    public WorldChunk() {
        id = -1;
        terrain = new Tile[Consts.T_H_CH][Consts.T_W_CH];
        for(int i = 0; i < Consts.T_H_CH; i++) {
            for(int j = 0; j < Consts.T_W_CH; j++) {
                terrain[i][j] = new Tile();
            }
        }

    }

    public void init(PacketInputStream in) {

        id = in.getInt();

        for(int i = 0; i < Consts.T_H_CH; i++) {
            for(int j = 0; j < Consts.T_W_CH; j++) {

                TileType type = TileType.getType(in.getByte());
                int stability = in.getByte();
                Tile t = terrain[i][j];
                t.init(id, j, i, stability, type);

                if(type == TileType.CAMPFIRE_BIG) {
                    GameScreen.get().getWorld().getActiveLights().add(new LightSource(LightType.TRANSP_SPHERE_MEDIUM, new Vector2(j*id, i)));
                }
            }
        }
    }

    @Override
    public void reset() {}

    public int getId() {
        return id;
    }

    public Tile[][] getTerrain() {
        return terrain;
    }

}
