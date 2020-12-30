package com.astetyne.expirium.main.world.tiles;

import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.items.Item;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public enum TileType {

    AIR(false, null, 0, null, 0, false),
    STONE(true, Res.STONE_TEXTURE, 10f, Item.STONE, 3, false),
    GRASS(true, Res.GRASS_TEXTURE, 5f, Item.GRASS, 2, false),
    DIRT(true, Res.DIRT_TEXTURE, 5f, Item.DIRT, 2, false),
    WOOD(false, Res.WOOD_TEXTURE, 3f, Item.RAW_WOOD, 2, false);

    private static final HashMap<Integer, TileType> map;
    static {
        map = new HashMap<>();
        int i = 0;
        for(TileType tt : TileType.values()) {
            tt.id = i;
            map.put(tt.id, tt);
            i++;
        }
    }

    public static TileType getType(int id) {
        return map.get(id);
    }

    int id;
    boolean solid;
    TextureRegion texture;
    float breakTime;
    Item dropItem;
    int stability;
    boolean onlyOnSolid;

    TileType(boolean solid, TextureRegion tex, float breakTime, Item dropItem, int stability, boolean b) {
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

    public Item getDropItem() {
        return dropItem;
    }

    public int getStability() {
        return stability;
    }
}
