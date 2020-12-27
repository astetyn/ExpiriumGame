package com.astetyne.expirium.main.items;

import com.astetyne.expirium.main.Resources;
import com.astetyne.expirium.main.world.tiles.TileType;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public enum ItemType {

    STONE(0, Resources.STONE_TEXTURE, 1, 1),
    GRASS(1, Resources.GRASS_TEXTURE, 1, 2),
    DIRT(2, Resources.DIRT_TEXTURE, 1, 3),
    RAW_WOOD(3, Resources.WOOD_TEXTURE, 1, 4);

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
    int buildTileID;

    ItemType(int id, TextureRegion itemTexture, int category, int buildTileID) {
        this.id = id;
        this.itemTexture = itemTexture;
        this.category = category;
        this.buildTileID = buildTileID;
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

    public TileType getBuildTile() {
        return TileType.getType(buildTileID);
    }
}
