package com.astetyne.expirium.main.items;

import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.world.tiles.TileType;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public enum Item {

    EMPTY(ItemCategory.EMPTY, -1, -1, -1, 0, null, null, "error", -1),
    STONE(ItemCategory.MATERIAL, 1, 1, 1, 0.5f, Res.STONE_ITEM, Res.STONE_ITEM, "Stone",1.1f),
    GRASS(ItemCategory.MATERIAL, 2, 1, 1, 0.5f, Res.GRASS_ITEM, Res.GRASS_ITEM, "Grass",1),
    DIRT(ItemCategory.MATERIAL, 3, 1, 1, 0.5f, Res.DIRT_ITEM, Res.DIRT_ITEM, "Dirt",1),
    RAW_WOOD(ItemCategory.MISC, 4, 1, 1, 0.5f, Res.RAW_WOOD_ITEM, Res.RAW_WOOD_ITEM, "Raw wood",1),
    PICKAXE(ItemCategory.TOOL, 0, 1, 2, 1, Res.PICKAXE_ITEM, Res.PICKAXE_ITEM, "Pickaxe",10),
    CAMPFIRE(ItemCategory.MATERIAL, 11, 2, 2, 2, Res.CAMPFIRE_ITEM, Res.CAMPFIRE_ITEM, "Campfire", 0.8f),
    WOODEN_WALL(ItemCategory.MATERIAL, 12, 1, 1, 0.5f, Res.WOODEN_WALL_ITEM, Res.WOODEN_WALL_ITEM, "Wooden wall", 1);

    private static final HashMap<Integer, Item> map;
    static {
        map = new HashMap<>();
        int i = 0;
        for(Item it : Item.values()) {
            it.id = i;
            map.put(it.id, it);
            i++;
        }
    }

    int id;
    ItemCategory category;
    int buildTileID;
    int gridWidth;
    int gridHeight;
    float weight;
    TextureRegion itemTexture;
    TextureRegion itemTextureInGrid;
    String label;
    float speedCoef;

    Item(ItemCategory cat, int tileID, int gw, int gh, float weight, TextureRegion tex, TextureRegion tex2, String label, float sc) {
        category = cat;
        buildTileID = tileID;
        gridWidth = gw;
        gridHeight = gh;
        this.weight = weight;
        this.itemTexture = tex;
        itemTextureInGrid = tex2;
        this.label = label;
        this.speedCoef = sc;
    }

    public static Item getType(int id) {
        return map.get(id);
    }

    public int getId() {
        return id;
    }

    public ItemCategory getCategory() {
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

    public float getSpeedCoef() {
        return speedCoef;
    }
}
