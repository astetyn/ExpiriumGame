package com.astetyne.main.world.tiles;

import com.astetyne.main.Resources;
import com.astetyne.main.items.ItemType;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public enum TileType {

    AIR(0, false, null, 0, null),
    STONE(1, true, Resources.STONE_TEXTURE, 1, ItemType.STONE),
    GRASS(2, true, Resources.GRASS_TEXTURE, 1, ItemType.GRASS),
    DIRT(3, true, Resources.DIRT_TEXTURE, 1, ItemType.DIRT);

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

    TileType(int id, boolean solid, TextureRegion texture, float breakTime, ItemType dropItem) {
        this.id = id;
        this.solid = solid;
        this.texture = texture;
        this.breakTime = breakTime;
        this.dropItem = dropItem;
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
}
