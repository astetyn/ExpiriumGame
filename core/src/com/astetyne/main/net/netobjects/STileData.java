package com.astetyne.main.net.netobjects;

import com.astetyne.main.world.TileType;

import java.io.Serializable;

public class STileData implements Serializable {

    private TileType type;

    public STileData(TileType type) {
        this.type = type;
    }

    public void setType(TileType type) {
        this.type = type;
    }

    public TileType getType() {
        return type;
    }
}
