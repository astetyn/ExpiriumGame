package com.astetyne.expirium.client.items;

import com.astetyne.expirium.client.tiles.TileType;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public enum Item {

    EMPTY(ItemCategory.EMPTY, -1, -1, 0, null, "error"),
    STONE(ItemCategory.MATERIAL, "STONE", 1, 1, 0.5f, "stone_item", "Stone"),
    RHYOLITE(ItemCategory.MATERIAL, "RHYOLITE", 1, 1, 0.2f, "rhyolite_item", "Rhyolite"),
    GRASS(ItemCategory.MATERIAL, "GRASS", 1, 1, 0.1f, "grass_item", "Grass"),
    DIRT(ItemCategory.MATERIAL, "DIRT", 1, 1, 0.1f, "dirt_item", "Dirt"),
    RAW_WOOD(ItemCategory.MISC, 1, 1, 0.05f, "raw_wood_item", "Raw wood"),
    RHYOLITE_PICKAXE(ItemCategory.TOOL, 1, 2, 1, "rhyolite_pickaxe_item", "pickaxe_item_grid", "Pickaxe"),
    CAMPFIRE(ItemCategory.MATERIAL, "CAMPFIRE_BIG", 2, 2, 0.5f, "campfire_item", "Campfire"),
    WOODEN_WALL(ItemCategory.MATERIAL, "WOODEN_WALL", 1, 1, 0.05f, "wooden_wall_item", "Wooden wall"),
    APPLE(ItemCategory.CONSUMABLE, 1, 1, 0.03f, "apple_item", "Apple"),
    COOKED_APPLE(ItemCategory.CONSUMABLE, 1, 1, 0.03f, "cooked_apple_item", "Cooked Apple"),
    RASPBERRY_BUSH(ItemCategory.MATERIAL, "RASPBERRY_BUSH_1", 1, 1, 0.1f, "raspberry_bush_item", "Raspberry bush"),
    RASPBERRY(ItemCategory.CONSUMABLE, 1, 1, 0.01f, "raspberry_item", "Raspberries"),
    WOODEN_BOWL(ItemCategory.MISC, 1, 1, 0.05f, "wooden_bowl_item", "Wooden bowl"),
    FRUIT_JAM(ItemCategory.CONSUMABLE, 1, 1, 0.1f, "fruit_jam_item", "Fruit jam"),
    COAL(ItemCategory.MISC, 1, 1, 0.1f, "coal_item", "Coal");

    ItemCategory category;
    String buildTile;
    int gridWidth;
    int gridHeight;
    float weight;
    String regionName, gridRegionName;
    TextureRegion texture, gridTexture;
    String label;

    Item(ItemCategory cat, int gw, int gh, float weight, String regionName, String label) {
        this(cat, "", gw, gh, weight, regionName, label);
    }

    Item(ItemCategory cat, String buildTile, int gw, int gh, float weight, String regionName, String label) {
        this(cat, buildTile, gw, gh, weight, regionName, regionName, label);
    }

    Item(ItemCategory cat, int gw, int gh, float weight, String regionName, String gridRegionName, String label) {
        this(cat, "", gw, gh, weight, regionName, gridRegionName, label);
    }

    Item(ItemCategory cat, String buildTile, int gw, int gh, float weight, String regionName, String gridRegionName, String label) {
        category = cat;
        this.buildTile = buildTile;
        gridWidth = gw;
        gridHeight = gh;
        this.weight = weight;
        this.regionName = regionName;
        this.gridRegionName = gridRegionName;
        this.label = label;
    }

    public static void loadTextures() {
        System.out.println("Loading textures for items.");
        TextureAtlas gui = new TextureAtlas("gui.atlas");
        for(Item item : values()) {
            item.texture = gui.findRegion(item.regionName);
            item.gridTexture = gui.findRegion(item.gridRegionName);
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

    public ItemCategory getCategory() {
        return category;
    }

    public TileType getBuildTile() {
        if(buildTile.equals("")) return null;
        return TileType.valueOf(buildTile);
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
