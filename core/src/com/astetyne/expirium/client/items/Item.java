package com.astetyne.expirium.client.items;

import com.astetyne.expirium.client.tiles.Material;
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
    GLASS(ItemCat.MATERIAL, 0.06f),
    CACTUS(ItemCat.MATERIAL, 0.05f),
    BLUEBERRY(ItemCat.CONSUMABLE, 0.01f),
    BLUEBERRY_BUSH(ItemCat.MATERIAL, 0.1f),
    FIR_CONE(ItemCat.MATERIAL, 0.02f),
    LIMESTONE(ItemCat.MISC, 0.1f),
    ;

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

    Item(ItemCat cat, float weight, boolean hasCustomGridTex) {
        this(cat, 1, 1, weight, hasCustomGridTex);
    }

    Item(ItemCat cat, int gw, int gh, float weight) {
        this(cat, gw, gh, weight, false);
    }

    Item(ItemCat cat, int gw, int gh, float weight, boolean hasCustomGridTex) {
        category = cat;
        gridWidth = gw;
        gridHeight = gh;
        this.weight = weight;
        this.hasCustomGridTex = hasCustomGridTex;

        StringBuilder sb = new StringBuilder(name().toLowerCase(Locale.US).replaceAll("_", " "));
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        label = sb.toString();
    }

    public static void loadTextures(TextureAtlas gui) {
        System.out.println("Loading textures for items.");
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

    public Material getBuildMaterial() {
        switch(this) {
            case STONE: return Material.STONE;
            case RHYOLITE: return Material.RHYOLITE;
            case DIRT: return Material.DIRT;
            case GRASS: return Material.GRASS;
            case WOODEN_WALL: return Material.WOODEN_WALL;
            case SOFT_WOODEN_WALL: return Material.SOFT_WOODEN_WALL;
            case RASPBERRY_BUSH: return Material.RASPBERRY_BUSH;
            case WOODEN_SUPPORT: return Material.WOODEN_SUPPORT;
            case CAMPFIRE: return Material.CAMPFIRE_BIG;
            case SAND: return Material.SAND;
            case GLASS: return Material.GLASS;
            case BLUEBERRY_BUSH: return Material.BLUEBERRY_BUSH;
            case CACTUS: return Material.CACTUS_DOUBLE;
            case APPLE: return Material.GROWING_PLANT_SHOREA;
            case FIR_CONE: return Material.GROWING_PLANT_FIR;
            default: return null;
        }
    }

    public boolean isMergeable() {
        switch(this) {
            case WOODEN_MATTOCK:
            case RHYOLITE_MATTOCK:
                return false;
            default: return true;
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
