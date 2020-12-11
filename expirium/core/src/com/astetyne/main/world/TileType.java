package com.astetyne.main.world;

import com.astetyne.main.world.tiles.AirTile;
import com.astetyne.main.world.tiles.StoneTile;

import java.io.Serializable;

public enum TileType implements Serializable {

    AIR(AirTile.class),
    STONE(StoneTile.class);

    private final Class<? extends Tile> tileClass;

    TileType(Class<? extends Tile> tileClass) {
        this.tileClass = tileClass;
    }

    public Tile createInstance() {
        Tile t = null;
        try {
            t = tileClass.getConstructor().newInstance();
        }catch(Exception e) {
            e.printStackTrace();
        }
        return t;
    }

}
