package com.astetyne.main.world.tiles.data;

import com.astetyne.main.items.ItemType;
import com.astetyne.main.world.TileType;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class TileExtraData {

    public abstract TextureRegion getTexture();

    public abstract boolean isSolid();

    public abstract TileType getType();

    public abstract float getDurability();

    public abstract ItemType getItemOnDrop();

}
