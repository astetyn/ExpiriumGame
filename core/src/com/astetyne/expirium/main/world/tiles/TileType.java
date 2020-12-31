package com.astetyne.expirium.main.world.tiles;

import com.astetyne.expirium.main.ExpiriumGame;
import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.items.Item;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public enum TileType {

    AIR(false, null, null, 0, null, 0, false),
    STONE(true, Res.STONE_TILE, 10f, Item.STONE, 3, false),
    GRASS(true, Res.GRASS_TILE, 5f, Item.GRASS, 2, false),
    DIRT(true, Res.DIRT_TILE, 5f, Item.DIRT, 2, false),
    TREE1(false, Res.TREE1_TILE, 3f, Item.RAW_WOOD, 2, false),
    TREE2(false, Res.TREE2_TILE, 3f, Item.RAW_WOOD, 2, false),
    TREE3(false, Res.TREE3_TILE, 3f, Item.RAW_WOOD, 2, false),
    TREE4(false, Res.TREE4_TILE, 3f, Item.RAW_WOOD, 2, false),
    TREE5(false, Res.TREE5_TILE, 3f, Item.RAW_WOOD, 2, false),
    TREE6(false, Res.TREE6_TILE, 3f, Item.RAW_WOOD, 2, false),
    CAMPFIRE(true, Res.CAMPFIRE_FULL_TILE, 0.5f, Item.CAMPFIRE, 1, true),
    WOODEN_WALL(true, Res.WOODEN_WALL_TILE, 2f, Item.WOODEN_WALL, 5, false);

    int id;
    boolean solid;
    TextureRegion texture;
    Animation<TextureRegion> anim;
    float breakTime;
    Item dropItem;
    int stability;
    boolean onlyOnSolid;

    TileType(boolean solid, Animation<TextureRegion> anim, float breakTime, Item dropItem, int stability, boolean b) {
        this(solid, null, anim, breakTime, dropItem, stability, b);
    }

    TileType(boolean solid, TextureRegion tex, float breakTime, Item dropItem, int stability, boolean b) {
        this(solid, tex, null, breakTime, dropItem, stability, b);
    }

    TileType(boolean solid, TextureRegion tex, Animation<TextureRegion> anim, float time, Item item, int s, boolean b) {
        this.solid = solid;
        this.texture = tex;
        this.anim = anim;
        this.breakTime = time;
        this.dropItem = item;
        this.stability = s;
        onlyOnSolid = b;
    }

    public static TileType getType(int id) {
        return map.get(id);
    }

    public int getID() {
        return id;
    }

    public boolean isSolid() {
        return solid;
    }

    public TextureRegion getTexture() {
        if(anim != null) {
            return anim.getKeyFrame(ExpiriumGame.get().getTime());
        }
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
}
