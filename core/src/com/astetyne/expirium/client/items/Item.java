package com.astetyne.expirium.client.items;

import com.astetyne.expirium.client.tiles.TileType;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.Locale;

public enum Item {

    // make sure texture with identical name+"_item" can be found in atlas

    EMPTY(ItemCat.EMPTY, 0),
    STONE(ItemCat.MATERIAL, 0.5f),
    RHYOLITE(ItemCat.MATERIAL, 0.2f),
    GRASS(ItemCat.MATERIAL, 0.1f),
    DIRT(ItemCat.MATERIAL, 0.1f),
    RAW_WOOD(ItemCat.MISC, 0.05f),
    WOODEN_MATTOCK(ItemCat.TOOL, 1, 2, 1, true),
    RHYOLITE_MATTOCK(ItemCat.TOOL, 1, 2, 1, true),
    CAMPFIRE(ItemCat.MATERIAL, 2, 2, 0.5f),
    WOODEN_WALL(ItemCat.MATERIAL, 0.05f),
    SOFT_WOODEN_WALL(ItemCat.MATERIAL, 0.04f),
    WOODEN_SUPPORT(ItemCat.MATERIAL, 0.02f),
    APPLE(ItemCat.CONSUMABLE, 0.03f),
    COOKED_APPLE(ItemCat.CONSUMABLE, 0.03f),
    RASPBERRY_BUSH(ItemCat.MATERIAL, 0.1f),
    RASPBERRY(ItemCat.CONSUMABLE, 0.01f),
    WOODEN_BOWL(ItemCat.MISC, 0.05f),
    FRUIT_JAM(ItemCat.CONSUMABLE, 0.1f),
    COAL(ItemCat.MISC, 0.1f),
    SAND(ItemCat.MATERIAL, 0.08f),
    GLASS(ItemCat.MATERIAL, 0.06f);

    ItemCat category;
    int gridWidth;
    int gridHeight;
    float weight;
    boolean hasCustomGridTex;
    TextureRegion texture, gridTexture;
    String label;

    Item(ItemCat cat, float weight) {
        this(cat, weight, false);
    }

    Item(ItemCat cat, float weight, boolean hasCustomGrid) {
        this(cat, 1, 1, weight, hasCustomGrid);
    }

    Item(ItemCat cat, int gw, int gh, float weight) {
        this(cat, gw, gh, weight, false);
    }

    Item(ItemCat cat, int gw, int gh, float weight, boolean hasCustomGrid) {
        category = cat;
        gridWidth = gw;
        gridHeight = gh;
        this.weight = weight;
        this.hasCustomGridTex = hasCustomGrid;

        StringBuilder sb = new StringBuilder(name().toLowerCase(Locale.US).replaceAll("_", " "));
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        label = sb.toString();
    }

    public static void loadTextures() {
        System.out.println("Loading textures for items.");
        TextureAtlas gui = new TextureAtlas("gui.atlas");
        for(Item item : values()) {
            if(item == EMPTY) continue;

            String texName = item.name().toLowerCase(Locale.US) + "_item";
            String gridTexName = texName;
            if(item.hasCustomGridTex) gridTexName += "_grid";

            item.texture = gui.findRegion(texName);
            item.gridTexture = gui.findRegion(gridTexName);

        }
    }

    int id;
    private static final HashMap<Integer, Item> map;
    static {
        System.out.println("Item class loading.");
        map = new HashMap<>();
        int i = 0;
        for(Item it : Item.values()) {
            it.id = i;
            map.put(it.id, it);
            i++;
        }
    }

    public static Item getType(int id) {
        return map.get(id);
    }

    public int getId() {
        return id;
    }

    public ItemCat getCategory() {
        return category;
    }

    public TileType getBuildTile() {
        switch(this) {
            case STONE: return TileType.STONE;
            case RHYOLITE: return TileType.RHYOLITE;
            case DIRT: return TileType.DIRT;
            case GRASS: return TileType.GRASS;
            case WOODEN_WALL: return TileType.WOODEN_WALL;
            case SOFT_WOODEN_WALL: return TileType.SOFT_WOODEN_WALL;
            case RASPBERRY_BUSH: return TileType.RASPBERRY_BUSH_1;
            case WOODEN_SUPPORT: return TileType.WOODEN_SUPPORT;
            case CAMPFIRE: return TileType.CAMPFIRE_BIG;
            case SAND: return TileType.SAND;
            case GLASS: return TileType.GLASS;
            default: return null;
        }
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

    public TextureRegion getTexture() {
        return texture;
    }

    public TextureRegion getGridTexture() {
        return gridTexture;
    }

    public String getLabel() {
        return label;
    }

}
