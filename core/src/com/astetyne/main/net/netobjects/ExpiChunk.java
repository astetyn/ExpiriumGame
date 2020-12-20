package com.astetyne.main.net.netobjects;

public class ExpiChunk {

    private final int id;
    private final ExpiTile[][] terrain;

    public ExpiChunk(int id, ExpiTile[][] terrain) {
        this.id = id;
        this.terrain = terrain;
    }

    public int getId() {
        return id;
    }

    public ExpiTile[][] getTerrain() {
        return terrain;
    }

}
