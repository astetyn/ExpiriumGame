package com.astetyne.expirium.main.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Item {

    private final ItemType type;
    private final TextureRegion texture;

    public Item(ItemType type, TextureRegion texture) {
        this.type = type;
        this.texture = texture;
    }

    public ItemType getType() {
        return type;
    }

    public TextureRegion getTexture() {
        return texture;
    }
}
