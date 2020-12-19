package com.astetyne.main.world.tiles.data;

import com.astetyne.main.Resources;
import com.astetyne.main.items.ItemType;
import com.astetyne.main.net.netobjects.STile;
import com.astetyne.main.world.TileType;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class DirtExtraData extends TileExtraData {

    public DirtExtraData() {}

    public DirtExtraData(STile data) {}

    @Override
    public TextureRegion getTexture() {
        return Resources.DIRT_TEXTURE;
    }

    @Override
    public boolean isSolid() {
        return true;
    }

    @Override
    public TileType getType() {
        return TileType.DIRT;
    }

    @Override
    public float getBreakTime() {
        return 0.1f;
    }

    @Override
    public ItemType getItemOnDrop() {
        return ItemType.DIRT;
    }

}
