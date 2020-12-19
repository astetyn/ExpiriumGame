package com.astetyne.main.net.netobjects;

import com.astetyne.main.world.TileType;

import java.io.Serializable;

public class STile implements Serializable {

    private TileType type;
    private int stability;

    public STile(TileType type) {
        this.type = type;
    }

    public void setType(TileType type) {
        this.type = type;
    }

    public TileType getType() {
        return type;
    }

    public int getStability() {
        return stability;
    }

    public void setStability(int stability) {
        this.stability = stability;
    }
}
