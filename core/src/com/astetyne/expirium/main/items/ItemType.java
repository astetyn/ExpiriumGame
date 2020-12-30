package com.astetyne.expirium.main.items;

import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.world.tiles.TileType;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public enum ItemType {

    STONE(0, 1, 1, 1, 1, 0.5f, Res.STONE_TEXTURE, Res.STONE_TEXTURE, "Stone"),
    GRASS(1, 1, 2, 1, 1, 0.5f, Res.GRASS_TEXTURE, Res.GRASS_TEXTURE, "Travicka pre \nzajacika"),
    DIRT(2, 1, 3, 1, 1, 0.5f, Res.DIRT_TEXTURE, Res.DIRT_TEXTURE, "Dirt"),
    RAW_WOOD(3, 1, 4, 1, 1, 0.5f, Res.WOOD_TEXTURE, Res.WOOD_TEXTURE, "Raw wood");

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
    String label;

    ItemType(int id, int cat, int tileID, int gw, int gh, float weight, TextureRegion tex, TextureRegion tex2, String label) {
        this.id = id;
        category = cat;
        buildTileID = tileID;
        gridWidth = gw;
        gridHeight = gh;
        this.weight = weight;
        this.itemTexture = tex;
        itemTextureInGrid = tex2;
        this.label = label;
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

    public String getLabel() {
        return label;
    }
}
