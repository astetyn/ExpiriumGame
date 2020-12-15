package com.astetyne.main.world.tiles;

import com.astetyne.main.net.netobjects.STileData;
import com.astetyne.main.world.tiles.data.AirExtraData;
import com.astetyne.main.world.tiles.data.StoneExtraData;
import com.astetyne.main.world.tiles.data.TileExtraData;

public class TileFactory {

    public static TileExtraData createExtraData(STileData sData) {

        switch(sData.getType()) {
            case STONE:
                return new StoneExtraData(sData);
            default:
                return new AirExtraData(sData);
        }
    }

}
