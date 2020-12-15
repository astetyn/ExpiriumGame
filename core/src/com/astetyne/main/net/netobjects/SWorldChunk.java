package com.astetyne.main.net.netobjects;

import java.io.Serializable;

public class SWorldChunk implements Serializable {

    private final int id;
    private final STileData[][] terrain;

    public SWorldChunk(int id, STileData[][] terrain) {
        this.id = id;
        this.terrain = terrain;
    }

    public int getId() {
        return id;
    }

    public STileData[][] getTerrain() {
        return terrain;
    }
}
