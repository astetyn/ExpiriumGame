package com.astetyne.main.world;

import com.astetyne.main.world.tiles.data.*;

public enum TileType {

    AIR(AirExtraData.class, 0),
    STONE(StoneExtraData.class, 3),
    GRASS(GrassExtraData.class, 1),
    DIRT(DirtExtraData.class, 1);

    Class<? extends TileExtraData> clazz;
    int stability;

    TileType(Class<? extends TileExtraData> clazz, int stability) {
        this.clazz = clazz;
        this.stability = stability;
    }

    public TileExtraData initDefaultData() {
        try {
            return clazz.newInstance();
        }catch(InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return new AirExtraData();
    }

    public int getDefaultStability() {
        return stability;
    }

}
