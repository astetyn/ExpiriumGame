package com.astetyne.expirium.main.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Item {

    private final ItemType type;

    public Item(ItemType type) {
        this.type = type;
    }

    public ItemType getType() {
        return type;
    }

    public TextureRegion getTexture() {
        return type.getItemTexture();
    }
}
