package com.astetyne.main.items;

import com.astetyne.main.Resources;
import com.astetyne.main.world.tiles.data.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public enum ItemType {

    STONE(StoneExtraData.class, Resources.STONE_TEXTURE, 1),
    GRASS(GrassExtraData.class, Resources.GRASS_TEXTURE, 1),
    DIRT(DirtExtraData.class, Resources.DIRT_TEXTURE, 1);

    Class<? extends TileExtraData> clazz;
    TextureRegion itemTexture;
    int category;

    ItemType(Class<? extends TileExtraData> clazz, TextureRegion itemTexture, int category) {
        this.clazz = clazz;
        this.itemTexture = itemTexture;
        this.category = category;
    }

    public TileExtraData initDefaultData() {
        try {
            return clazz.newInstance();
        }catch(InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return new AirExtraData();
    }

    public Item initItem() {
        return new Item(this, itemTexture);
    }

    public int getCategory() {
        return category;
    }

}
