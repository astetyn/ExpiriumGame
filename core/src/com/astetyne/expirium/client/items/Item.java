package com.astetyne.expirium.client.items;

import com.astetyne.expirium.client.tiles.Material;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.Locale;

public enum Item {

    // make sure texture with identical name+"_item" can be found in atlas

    EMPTY(ItemCat.EMPTY, 0),
    LIMESTONE(ItemCat.MATERIAL, 0.08f),
    RHYOLITE(ItemCat.MATERIAL, 0.07f),
    MAGNETITE(ItemCat.MISC, 0.07f),
    CHROMITE(ItemCat.MISC, 0.07f),
    GRASS(ItemCat.MATERIAL, 0.02f),
    DIRT(ItemCat.MATERIAL, 0.08f),
    RAW_WOOD(ItemCat.MISC, 0.04f),
    WOODEN_MATTOCK(ItemCat.TOOL, 1, 2, 0.3f, true),
    RHYOLITE_MATTOCK(ItemCat.TOOL, 1, 2, 0.4f, true),
    CAMPFIRE(ItemCat.MATERIAL, 2, 2, 0.1f),
    WOODEN_WALL(ItemCat.MATERIAL, 0.05f),
    SOFT_WOODEN_WALL(ItemCat.MATERIAL, 0.04f),
    WOODEN_SUPPORT(ItemCat.MATERIAL, 0.02f),
    APPLE(ItemCat.CONSUMABLE, 0.03f),
    COOKED_APPLE(ItemCat.CONSUMABLE, 0.03f),
    RASPBERRY_BUSH(ItemCat.MATERIAL, 0.1f),
    RASPBERRY(ItemCat.CONSUMABLE, 0.01f),
    WOODEN_BOWL(ItemCat.MISC, 0.05f),
    FRUIT_JAM(ItemCat.CONSUMABLE, 0.05f),
    COAL(ItemCat.MISC, 0.05f),
    SAND(ItemCat.MATERIAL, 0.08f),
    GLASS(ItemCat.MATERIAL, 0.06f),
    CACTUS(ItemCat.MATERIAL, 0.05f),
    BLUEBERRY(ItemCat.CONSUMABLE, 0.01f),
    BLUEBERRY_BUSH(ItemCat.MATERIAL, 0.1f),
    FIR_CONE(ItemCat.MATERIAL, 0.02f),
    CLAYSTONE(ItemCat.MISC, 0.05f),
    FURNACE(ItemCat.MATERIAL, 2, 2, 0.5f),
    JAR(ItemCat.MISC, 0.05f),
    JAR_BLUEBERRY_JAM(ItemCat.CONSUMABLE, 0.06f),
    HUNTING_KNIFE(ItemCat.TOOL, 1, 2, 0.2f, true),
    TORCH(ItemCat.MATERIAL, 0.02f),
    CHEST(ItemCat.MATERIAL, 0.05f),
    SMALL_MEAT_RAW(ItemCat.CONSUMABLE, 0.01f),
    SMALL_MEAT_COOKED(ItemCat.CONSUMABLE, 0.01f),
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

            System.out.println("loaded item "+item+" with tex: "+item.texture);

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
            case LIMESTONE: return Material.LIMESTONE;
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
            case CACTUS: return Material.CACTUS_PLANT;
            case APPLE: return Material.GROWING_PLANT_SHOREA;
            case FIR_CONE: return Material.GROWING_PLANT_FIR;
            case FURNACE: return Material.FURNACE_OFF;
            case CHEST: return Material.CHEST;
            case TORCH: return Material.TORCH;
            default: return null;
        }
    }

    public boolean isWeapon() {
        switch(this) {
            case HUNTING_KNIFE:
                return true;
            default: return false;
        }
    }

    public boolean isTileBreaker() {
        switch(this) {
            case WOODEN_MATTOCK:
            case RHYOLITE_MATTOCK:
                return true;
            default: return false;
        }
    }

    public float getBreakingSpeedCoef() {
        switch(this) {
            case WOODEN_MATTOCK: return 2;
            case RHYOLITE_MATTOCK: return 3;
            default: return 1;
        }
    }

    public int getWeaponDamage() {
        switch(this) {
            case HUNTING_KNIFE: return 10;
            default: return 1;
        }
    }

    public boolean isMergeable() {
        if(category == ItemCat.TOOL) return false;
        switch(this) {
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
