package com.astetyne.expirium.client.tiles;

import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.resources.Textureable;
import com.astetyne.expirium.client.resources.TileTex;
import com.astetyne.expirium.client.resources.TileTexAnim;
import com.astetyne.expirium.server.core.world.ExpiWorld;
import com.astetyne.expirium.server.core.world.tile.ExpiTile;
import com.astetyne.expirium.server.core.world.tile.MetaTile;
import com.astetyne.expirium.server.core.world.tile.TileFix;
import com.astetyne.expirium.server.core.world.tile.meta.MetaTileCampfire;
import com.astetyne.expirium.server.core.world.tile.meta.MetaTileLeaves;
import com.astetyne.expirium.server.core.world.tile.meta.MetaTileRaspberryBush;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public enum Material implements Textureable {

    AIR(null, MetaTile.class, TileFix.SOFT, Solidity.LABILE, 0, 0),
    STONE(TileTex.STONE, MetaTile.class, TileFix.FULL, Solidity.SOLID, 4, 10, Item.STONE),
    RHYOLITE(TileTex.RHYOLITE, MetaTile.class, TileFix.FULL, Solidity.SOLID, 3, 6, Item.RHYOLITE),
    GRASS(TileTex.GRASS, MetaTile.class, TileFix.FULL, Solidity.SOLID, 2, 4, Item.GRASS),
    GRASS_SLOPE_R(TileTex.GRASS_SLOPE_R, MetaTile.class, TileFix.GRASS_SLOPE_R, Solidity.LABILE, 1, 3, Item.GRASS),
    GRASS_SLOPE_L(TileTex.GRASS_SLOPE_L, MetaTile.class, TileFix.GRASS_SLOPE_L, Solidity.LABILE, 1, 3, Item.GRASS),
    DIRT(TileTex.DIRT, MetaTile.class, TileFix.FULL, Solidity.SOLID, 2, 4, Item.DIRT),
    LOG1(TileTex.TREE1, MetaTile.class, TileFix.SOFT, Solidity.SOLID_VERT, 1, 3, Item.RAW_WOOD),
    LOG2(TileTex.TREE2, MetaTile.class, TileFix.SOFT, Solidity.SOLID_VERT, 1, 3, Item.RAW_WOOD),
    LOG3(TileTex.TREE3, MetaTile.class, TileFix.SOFT, Solidity.SOLID_VERT, 1, 3, Item.RAW_WOOD),
    LEAVES1(TileTex.TREE4, MetaTileLeaves.class, TileFix.SOFT, Solidity.LABILE, 1, 1),
    LEAVES2(TileTex.TREE5, MetaTileLeaves.class, TileFix.SOFT, Solidity.LABILE, 1, 1),
    LEAVES3(TileTex.TREE6, MetaTileLeaves.class, TileFix.SOFT, Solidity.LABILE, 1, 1),
    CAMPFIRE_SMALL(TileTexAnim.CAMPFIRE_SMALL, MetaTileCampfire.class, TileFix.CAMPFIRE, Solidity.LABILE_VERT, 1, 0.5f),
    CAMPFIRE_BIG(TileTexAnim.CAMPFIRE_BIG, MetaTileCampfire.class, TileFix.CAMPFIRE, Solidity.LABILE_VERT, 1, 0.5f),
    WOODEN_WALL(TileTex.WOODEN_WALL, MetaTile.class, TileFix.FULL, Solidity.SOLID, 6, 2, Item.WOODEN_WALL),
    SOFT_WOODEN_WALL(TileTex.SOFT_WOODEN_WALL, MetaTile.class, TileFix.SOFT, Solidity.SOLID, 6, 2, Item.SOFT_WOODEN_WALL),
    WOODEN_SUPPORT(TileTex.WOODEN_SUPPORT, MetaTile.class, TileFix.SOFT, Solidity.SOLID_VERT, 6, 1),
    RASPBERRY_BUSH_1(TileTex.RASPBERRY_BUSH_1, MetaTileRaspberryBush.class, TileFix.SOFT, Solidity.LABILE_VERT, 1, 1),
    RASPBERRY_BUSH_2(TileTex.RASPBERRY_BUSH_2, MetaTileRaspberryBush.class, TileFix.SOFT, Solidity.LABILE_VERT, 1, 1),
    COAL_ORE(TileTex.COAL_ORE, MetaTile.class, TileFix.FULL, Solidity.SOLID, 3, 10, Item.COAL),
    SAND(TileTex.SAND, MetaTile.class, TileFix.FULL, Solidity.SOLID_VERT, 1, 3, Item.SAND),
    GLASS(TileTex.GLASS, MetaTile.class, TileFix.FULL, Solidity.SOLID, 2, 1, Item.GLASS);

    private final Textureable textureable;
    private final Class<? extends MetaTile> metaClazz;
    private final TileFix tileFix;
    private final Solidity solidity;
    private final int maxStability;
    private final float breakTime;
    private final Item defaultDropItem; // ignore this if dropItems will be overridden in meta

    Material(Textureable tex, Class<? extends MetaTile> clazz, TileFix fix, Solidity s, int ms, float bt) {
        this(tex, clazz, fix, s, ms, bt, null);
    }

    Material(Textureable tex, Class<? extends MetaTile> clazz, TileFix fix, Solidity s, int ms, float bt, Item dpi) {
        this.textureable = tex;
        this.metaClazz = clazz;
        this.tileFix = fix;
        this.solidity = s;
        this.maxStability = ms;
        this.breakTime = bt;
        defaultDropItem = dpi;
    }

    public MetaTile init(ExpiWorld w, ExpiTile t) {
        try {
            return metaClazz.getConstructor(ExpiWorld.class, ExpiTile.class).newInstance(w, t);
        }catch(InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return new MetaTile(w, t);
    }

    public MetaTile init(ExpiWorld w, ExpiTile t, DataInputStream in) throws IOException {
        try {
            return metaClazz.getConstructor(ExpiWorld.class, ExpiTile.class, DataInputStream.class)
                    .newInstance(w, t, in);
        }catch(InstantiationException | NoSuchMethodException | IllegalAccessException e) {
            return init(w, t);
        }catch(InvocationTargetException e) {
            e.printStackTrace();
            throw (IOException) e.getCause(); // idk if this is correct
        }
    }

    public TextureRegion getTex() {
        return textureable.getTex();
    }

    public TileFix getFix() {
        return tileFix;
    }

    public Solidity getSolidity() {
        return solidity;
    }

    public int getMaxStability() {
        return maxStability;
    }

    public float getBreakTime() {
        return breakTime;
    }

    public Item getDefaultDropItem() {
        return defaultDropItem;
    }

    public boolean isTransparent() {
        if(tileFix == TileFix.SOFT) return true;
        switch(this) {
            case GLASS: return true;
            default: return false;
        }
    }

    public boolean isWall() {
        switch(this) {
            case WOODEN_WALL:
            case SOFT_WOODEN_WALL:
            case GLASS: return true;
            default: return false;
        }
    }

    public static Material getMaterial(int id) {
        return map.get(id);
    }

    public int getID() {
        return id;
    }

    int id;
    private static final HashMap<Integer, Material> map;
    static {
        System.out.println("TileType class loading.");
        map = new HashMap<>();
        int i = 0;
        for(Material tt : Material.values()) {
            tt.id = i;
            map.put(tt.id, tt);
            i++;
        }
    }
}
