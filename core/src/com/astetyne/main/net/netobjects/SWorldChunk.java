package com.astetyne.main.net.netobjects;

import java.io.Serializable;

public class SWorldChunk implements Serializable {

    private final int id;
    private final STile[][] terrain;

    public SWorldChunk(int id, STile[][] terrain) {
        this.id = id;
        this.terrain = terrain;
    }

    public int getId() {
        return id;
    }

    public STile[][] getTerrain() {
        return terrain;
    }
}
