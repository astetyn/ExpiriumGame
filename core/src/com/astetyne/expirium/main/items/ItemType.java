package com.astetyne.expirium.main.items;

import com.astetyne.expirium.main.Resources;
import com.astetyne.expirium.main.world.tiles.TileType;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public enum ItemType {

    STONE(0, 1, 1, 1, 1, 0.5f, Resources.STONE_TEXTURE, Resources.STONE_TEXTURE),
    GRASS(1, 1, 2, 1, 1, 0.5f, Resources.GRASS_TEXTURE, Resources.GRASS_TEXTURE),
    DIRT(2, 1, 3, 1, 1, 0.5f, Resources.DIRT_TEXTURE, Resources.DIRT_TEXTURE),
    RAW_WOOD(3, 1, 4, 1, 1, 0.5f, Resources.WOOD_TEXTURE, Resources.WOOD_TEXTURE);

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
    int category;
    int buildTileID;
    int gridWidth;
    int gridHeight;
    float weight;
    TextureRegion itemTexture;
    TextureRegion itemTextureInGrid;

    ItemType(int id, int cat, int tileID, int gw, int gh, float weight, TextureRegion tex, TextureRegion tex2) {
        this.id = id;
        category = cat;
        buildTileID = tileID;
        gridWidth = gw;
        gridHeight = gh;
        this.weight = weight;
        this.itemTexture = tex;
        itemTextureInGrid = tex2;
    }

    public int getId() {
        return id;
    }

    public Item initItem() {
        return new Item(this);
    }

    public int getCategory() {
        return category;
    }

    public TileType getBuildTile() {
        return TileType.getType(buildTileID);
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public float getWeight() {
        return weight;
    }

    public TextureRegion getItemTexture() {
        return itemTexture;
    }

    public TextureRegion getItemTextureInGrid() {
        return itemTextureInGrid;
    }
}
