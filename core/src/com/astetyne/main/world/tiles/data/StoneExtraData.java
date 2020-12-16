package com.astetyne.main.world.tiles.data;

import com.astetyne.main.Resources;
import com.astetyne.main.net.netobjects.STileData;
import com.astetyne.main.world.TileType;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class StoneExtraData extends TileExtraData {

    public StoneExtraData(STileData data) {

    }

    @Override
    public TextureRegion getTexture() {
        return Resources.STONE_TEXTURE;
    }

    @Override
    public boolean isSolid() {
        return true;
    }

    @Override
    public TileType getType() {
        return TileType.STONE;
    }

    @Override
    public float getDurability() {
        return 2;
    }

}
