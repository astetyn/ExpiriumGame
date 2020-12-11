package com.astetyne.main.net.netobjects;

import com.astetyne.main.world.TileType;

import java.io.Serializable;

public class SWorldChunk implements Serializable {

    private final int id;
    private final TileType[][] terrain;

    public SWorldChunk(int id, TileType[][] terrain) {
        this.id = id;
        this.terrain = terrain;
    }

    public int getId() {
        return id;
    }

    public TileType[][] getTerrain() {
        return terrain;
    }
}
