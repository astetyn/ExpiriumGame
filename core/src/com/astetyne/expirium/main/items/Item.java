package com.astetyne.expirium.main.items;

import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.world.tiles.TileType;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public enum Item {

    STONE(1, 1, 1, 1, 0.5f, Res.STONE_TEXTURE, Res.STONE_TEXTURE, "Stone",1),
    GRASS(1, 2, 1, 1, 0.5f, Res.GRASS_TEXTURE, Res.GRASS_TEXTURE, "Travicka pre zajacika",1),
    DIRT(1, 3, 1, 1, 0.5f, Res.DIRT_TEXTURE, Res.DIRT_TEXTURE, "Dirt",1),
    RAW_WOOD(4, 4, 1, 1, 0.5f, Res.WOOD_TEXTURE, Res.WOOD_TEXTURE, "Raw wood",1),
    PICKAXE(0, 0, 1, 2, 1, Res.PICKAXE_TEXTURE, Res.PICKAXE_TEXTURE, "Pickaxe",10);

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
    int category;
    int buildTileID;
    int gridWidth;
    int gridHeight;
    float weight;
    TextureRegion itemTexture;
    TextureRegion itemTextureInGrid;
    String label;
    float speedCoef;

    Item(int cat, int tileID, int gw, int gh, float weight, TextureRegion tex, TextureRegion tex2, String label, float sc) {
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

    public float getSpeedCoef() {
        return speedCoef;
    }
}
