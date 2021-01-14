package com.astetyne.expirium.client.tiles;

import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.resources.Textureable;
import com.astetyne.expirium.client.resources.TileTex;
import com.astetyne.expirium.client.resources.TileTexAnim;
import com.astetyne.expirium.server.backend.FixRes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public enum TileType implements Textureable {

    AIR(Solidity.LABILE_SOFT, null, 0, Item.EMPTY, 0),
    STONE(Solidity.SOLID, TileTex.STONE, 10f, Item.STONE, 3),
    RHYOLITE(Solidity.SOLID, TileTex.RHYOLITE, 5f, Item.STONE, 3),
    GRASS(Solidity.SOLID, TileTex.GRASS, 5f, Item.GRASS, 2),
    GRASS_SLOPE_R(Solidity.LABILE_SHAPE, TileTex.GRASS_SLOPE_R, 4f, Item.GRASS, 1, FixRes.GRASS_SLOPE_R),
    GRASS_SLOPE_L(Solidity.LABILE_SHAPE, TileTex.GRASS_SLOPE_L, 4f, Item.GRASS, 1, FixRes.GRASS_SLOPE_L),
    DIRT(Solidity.SOLID, TileTex.DIRT, 5f, Item.DIRT, 2),
    TREE1(Solidity.SOLID_SOFT, TileTex.TREE1, 3f, Item.RAW_WOOD, 2),
    TREE2(Solidity.SOLID_SOFT, TileTex.TREE2, 3f, Item.RAW_WOOD, 2),
    TREE3(Solidity.SOLID_SOFT, TileTex.TREE3, 3f, Item.RAW_WOOD, 2),
    TREE4(Solidity.SOLID_SOFT, TileTex.TREE4, 3f, Item.RAW_WOOD, 2),
    TREE5(Solidity.SOLID_SOFT, TileTex.TREE5, 3f, Item.RAW_WOOD, 2),
    TREE6(Solidity.SOLID_SOFT, TileTex.TREE6, 3f, Item.RAW_WOOD, 2),
    CAMPFIRE_SMALL(Solidity.LABILE_SHAPE, TileTexAnim.CAMPFIRE_SMALL, 0.5f, Item.EMPTY, 1, FixRes.CAMPFIRE),
    CAMPFIRE_BIG(Solidity.LABILE_SHAPE, TileTexAnim.CAMPFIRE_BIG, 0.5f, Item.EMPTY, 1, FixRes.CAMPFIRE),
    WOODEN_WALL(Solidity.SOLID, TileTex.WOODEN_WALL, 2f, Item.WOODEN_WALL, 5);

    Solidity solidity;
    Textureable textureable;
    float breakTime;
    ItemDropper itemDropper;
    int maxStability;
    FixRes.EdgesData edgesData;

    TileType(Solidity ss, Textureable textureable, float time, Item item, int s) {
        this(ss, textureable, time, new ItemDropper(new Item[]{item}, 1), s, null);
    }

    // this is only for case when solidity is not SHAPE
    TileType(Solidity ss, Textureable textureable, float time, ItemDropper itemDropper, int s) {
        this(ss, textureable, time, itemDropper, s, null);
    }

    TileType(Solidity ss, Textureable textureable, float time, Item item, int s, FixRes.EdgesData fd) {
        this(ss, textureable, time, new ItemDropper(new Item[]{item}, 1), s, fd);
    }

    TileType(Solidity ss, Textureable textureable, float time, ItemDropper itemDropper, int s, FixRes.EdgesData fd) {
        this.solidity = ss;
        this.textureable = textureable;
        this.breakTime = time;
        this.itemDropper = itemDropper;
        this.maxStability = s;
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

    public TextureRegion getTex() {
        return textureable.getTex();
    }

    public float getBreakTime() {
        return breakTime;
    }

    public ItemDropper getItemDropper() {
        return itemDropper;
    }

    public int getMaxStability() {
        return maxStability;
    }

    public FixRes.EdgesData getEdgesData() {
        return edgesData;
    }

    int id;
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
