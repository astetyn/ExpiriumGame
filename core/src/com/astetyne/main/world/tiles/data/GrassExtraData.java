package com.astetyne.main.world.tiles.data;

import com.astetyne.main.Resources;
import com.astetyne.main.items.ItemType;
import com.astetyne.main.net.netobjects.STile;
import com.astetyne.main.world.TileType;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GrassExtraData extends TileExtraData {

    public GrassExtraData() {}

    public GrassExtraData(STile data) {}

    @Override
    public TextureRegion getTexture() {
        return Resources.GRASS_TEXTURE;
    }

    @Override
    public boolean isSolid() {
        return true;
    }

    @Override
    public TileType getType() {
        return TileType.GRASS;
    }

    @Override
    public float getBreakTime() {
        return 1;
    }

    @Override
    public ItemType getItemOnDrop() {
        return ItemType.GRASS;
    }

}
