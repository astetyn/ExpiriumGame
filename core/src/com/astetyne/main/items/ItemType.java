package com.astetyne.main.items;

import com.astetyne.main.Resources;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public enum ItemType {

    STONE(0, Resources.STONE_TEXTURE, 1),
    GRASS(1, Resources.GRASS_TEXTURE, 1),
    DIRT(2, Resources.DIRT_TEXTURE, 1);

    private static final HashMap<Integer, ItemType> map;

    static {
        map = new HashMap<>();
        for(ItemType it : ItemType.values()) {
            map.put(it.id, it);
        }
    }

    public static ItemType getType(int id) {
        return map.get(id);
    }

    int id;
    TextureRegion itemTexture;
    int category;

    ItemType(int id, TextureRegion itemTexture, int category) {
        this.id = id;
        this.itemTexture = itemTexture;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public Item initItem() {
        return new Item(this, itemTexture);
    }

    public int getCategory() {
        return category;
    }

}
