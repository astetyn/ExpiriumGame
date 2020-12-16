package com.astetyne.main.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ItemStack {

    private ItemType type;
    private TextureRegion texture;

    public ItemType getType() {
        return type;
    }

    public TextureRegion getTexture() {
        return texture;
    }
}
