package com.astetyne.expirium.main.world.tiles;

import com.astetyne.expirium.main.Resources;
import com.astetyne.expirium.main.items.ItemType;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public enum TileType {

    AIR(0, false, null, 0, null, 0),
    STONE(1, true, Resources.STONE_TEXTURE, 0.1f, ItemType.STONE, 3),
    GRASS(2, true, Resources.GRASS_TEXTURE, 0.1f, ItemType.GRASS, 2),
    DIRT(3, true, Resources.DIRT_TEXTURE, 0.1f, ItemType.DIRT, 2);

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

    TileType(int id, boolean solid, TextureRegion texture, float breakTime, ItemType dropItem, int stability) {
        this.id = id;
        this.solid = solid;
        this.texture = texture;
        this.breakTime = breakTime;
        this.dropItem = dropItem;
        this.stability = stability;
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
