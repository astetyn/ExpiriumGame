package com.astetyne.expirium.main.world.tiles;

import com.astetyne.expirium.main.Resources;
import com.astetyne.expirium.main.items.ItemType;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public enum TileType {

    AIR(0, false, null, 0, null, 0, false),
    STONE(1, true, Resources.STONE_TEXTURE, 1f, ItemType.STONE, 3, false),
    GRASS(2, true, Resources.GRASS_TEXTURE, 0.5f, ItemType.GRASS, 2, false),
    DIRT(3, true, Resources.DIRT_TEXTURE, 0.5f, ItemType.DIRT, 2, false),
    WOOD(4, false, Resources.WOOD_TEXTURE, 0.3f, ItemType.RAW_WOOD, 2, false);

    private static final HashMap<Integer, TileType> map;

    static {
        map = new HashMap<>();
        for(TileType tt : TileType.values()) {
            map.put(tt.id, tt);
        }
    }

    public static TileType getType(int id) {
        return map.get(id);
    }

    int id;
    boolean solid;
    TextureRegion texture;
    float breakTime;
    ItemType dropItem;
    int stability;
    boolean onlyOnSolid;

    TileType(int id, boolean solid, TextureRegion tex, float breakTime, ItemType dropItem, int stability, boolean b) {
        this.id = id;
        this.solid = solid;
        this.texture = tex;
        this.breakTime = breakTime;
        this.dropItem = dropItem;
        this.stability = stability;
        onlyOnSolid = b;
    }

    public int getID() {
        return id;
    }

    public boolean isSolid() {
        return solid;
    }

    public TextureRegion getTexture() {
        return texture;
    }

    public float getBreakTime() {
        return breakTime;
    }

    public ItemType getDropItem() {
        return dropItem;
    }

    public int getStability() {
        return stability;
    }
}
