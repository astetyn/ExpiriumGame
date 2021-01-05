package com.astetyne.expirium.main.world.tiles;

import com.astetyne.expirium.main.ExpiGame;
import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.items.Item;
import com.astetyne.expirium.server.backend.FixRes;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public enum TileType {

    AIR(Solidity.LABILE_SOFT, null, null, 0, null, 0, false, null),
    STONE(Solidity.SOLID, Res.STONE_TILE, 10f, Item.STONE, 3, false, null),
    GRASS(Solidity.SOLID, Res.GRASS_TILE, 5f, Item.GRASS, 2, false, null),
    DIRT(Solidity.SOLID, Res.DIRT_TILE, 5f, Item.DIRT, 2, false, null),
    TREE1(Solidity.SOLID_SOFT, Res.TREE1_TILE, 3f, Item.RAW_WOOD, 2, true, null),
    TREE2(Solidity.SOLID_SOFT, Res.TREE2_TILE, 3f, Item.RAW_WOOD, 2, false, null),
    TREE3(Solidity.SOLID_SOFT, Res.TREE3_TILE, 3f, Item.RAW_WOOD, 2, false, null),
    TREE4(Solidity.SOLID_SOFT, Res.TREE4_TILE, 3f, Item.RAW_WOOD, 2, false, null),
    TREE5(Solidity.SOLID_SOFT, Res.TREE5_TILE, 3f, Item.RAW_WOOD, 2, false, null),
    TREE6(Solidity.SOLID_SOFT, Res.TREE6_TILE, 3f, Item.RAW_WOOD, 2, false, null),
    CAMPFIRE_SMALL(Solidity.LABILE_SHAPE, Res.CAMPFIRE_SMALL_TILE, 0.5f, null, 1, true, FixRes.CAMPFIRE),
    CAMPFIRE_BIG(Solidity.LABILE_SHAPE, Res.CAMPFIRE_BIG_TILE, 0.5f, null, 1, true, FixRes.CAMPFIRE),
    WOODEN_WALL(Solidity.SOLID, Res.WOODEN_WALL_TILE, 2f, Item.WOODEN_WALL, 5, false, null);

    int id;
    Solidity solidity;
    TextureRegion texture;
    Animation<TextureRegion> anim;
    float breakTime;
    Item dropItem;
    int stability;
    boolean onlyOnSolid;
    FixRes.EdgesData edgesData;

    TileType(Solidity ss, Animation<TextureRegion> anim, float time, Item item, int s, boolean b, FixRes.EdgesData fd) {
        this(ss, null, anim, time, item, s, b, fd);
    }

    TileType(Solidity ss, TextureRegion tex, float time, Item item, int s, boolean b, FixRes.EdgesData fd) {
        this(ss, tex, null, time, item, s, b, fd);
    }

    TileType(Solidity ss, TextureRegion tex, Animation<TextureRegion> anim, float time, Item item, int s, boolean b, FixRes.EdgesData fd) {
        this.solidity = ss;
        this.texture = tex;
        this.anim = anim;
        this.breakTime = time;
        this.dropItem = item;
        this.stability = s;
        onlyOnSolid = b;
        edgesData = fd;
    }

    public static TileType getType(int id) {
        return map.get(id);
    }

    public int getID() {
        return id;
    }

    public Solidity getSolidity() {
        return solidity;
    }

    public TextureRegion getTexture() {
        if(anim != null) {
            return anim.getKeyFrame(ExpiGame.get().getTime());
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

    public boolean isOnlyOnSolid() {
        return onlyOnSolid;
    }

    public FixRes.EdgesData getEdgesData() {
        return edgesData;
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
