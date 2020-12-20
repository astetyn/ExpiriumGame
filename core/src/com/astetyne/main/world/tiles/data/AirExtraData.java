package com.astetyne.main.world.tiles.data;

import com.astetyne.main.items.ItemType;
import com.astetyne.main.net.netobjects.ExpiTile;
import com.astetyne.main.world.TileType;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AirExtraData extends TileExtraData {

    public AirExtraData() {}

    public AirExtraData(ExpiTile data) {}

    @Override
    public TextureRegion getTexture() {
        return null;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public TileType getType() {
        return TileType.AIR;
    }

    @Override
    public float getBreakTime() {
        return 0;
    }

    @Override
    public ItemType getItemOnDrop() {
        return null;
    }

}
