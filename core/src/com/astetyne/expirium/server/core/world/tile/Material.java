package com.astetyne.expirium.server.core.world.tile;

import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.resources.Textureable;
import com.astetyne.expirium.client.resources.TileTex;
import com.astetyne.expirium.client.resources.TileTexAnim;
import com.astetyne.expirium.server.core.world.World;
import com.astetyne.expirium.server.core.world.tile.meta.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public enum Material implements Textureable {

    AIR(null, MetaTile.class, TileFix.SOFT, Solidity.LABILE, 0, 0),
    LIMESTONE(TileTex.LIMESTONE, MetaTile.class, TileFix.FULL, Solidity.SOLID, 6, 10, Item.LIMESTONE),
    RHYOLITE(TileTex.RHYOLITE, MetaTile.class, TileFix.FULL, Solidity.SOLID, 4, 6, Item.RHYOLITE),
    MAGNETITE(TileTex.MAGNETITE, MetaTile.class, TileFix.FULL, Solidity.SOLID, 5, 12, Item.MAGNETITE),
    CHROMITE(TileTex.CHROMITE, MetaTile.class, TileFix.FULL, Solidity.SOLID, 5, 12, Item.CHROMITE),
    GRASS(TileTex.GRASS, MetaTile.class, TileFix.FULL, Solidity.SOLID, 4, 4, Item.GRASS),
    GRASS_SLOPE_R(TileTex.GRASS_SLOPE_RIGHT, MetaTile.class, TileFix.SLOPE_R, Solidity.LABILE, 1, 3, Item.GRASS),
    GRASS_SLOPE_L(TileTex.GRASS_SLOPE_LEFT, MetaTile.class, TileFix.SLOPE_L, Solidity.LABILE, 1, 3, Item.GRASS),
    DIRT(TileTex.DIRT, MetaTile.class, TileFix.FULL, Solidity.SOLID, 4, 4, Item.DIRT),
    LOG_SHOREA(TileTex.LOG_SHOREA, MetaTile.class, TileFix.SOFT, Solidity.ONLY_VERT, 2, 3, Item.RAW_WOOD),
    LOG_SHOREA_RIGHT(TileTex.LOG_SHOREA_RIGHT, MetaTile.class, TileFix.SOFT, Solidity.SOLID_VERT, 2, 3, Item.RAW_WOOD),
    LOG_SHOREA_LEFT(TileTex.LOG_SHOREA_LEFT, MetaTile.class, TileFix.SOFT, Solidity.SOLID_VERT, 2, 3, Item.RAW_WOOD),
    LEAVES_SHOREA_TOP(TileTex.LEAVES_SHOREA_TOP, MetaTileLeaves.class, TileFix.SOFT, Solidity.LABILE, 1, 1),
    LEAVES_SHOREA_LEFT(TileTex.LEAVES_SHOREA_LEFT, MetaTileLeaves.class, TileFix.SOFT, Solidity.LABILE, 1, 1),
    LEAVES_SHOREA_RIGHT(TileTex.LEAVES_SHOREA_RIGHT, MetaTileLeaves.class, TileFix.SOFT, Solidity.LABILE, 1, 1),
    CAMPFIRE_SMALL(TileTexAnim.CAMPFIRE_SMALL, MetaTileCampfire.class, TileFix.CAMPFIRE, Solidity.LABILE_VERT, 1, 0.5f),
    CAMPFIRE_BIG(TileTexAnim.CAMPFIRE_BIG, MetaTileCampfire.class, TileFix.CAMPFIRE, Solidity.LABILE_VERT, 1, 0.5f),
    WOODEN_WALL(TileTex.WOODEN_WALL, MetaTile.class, TileFix.FULL, Solidity.SOLID, 8, 2, Item.WOODEN_WALL),
    SOFT_WOODEN_WALL(TileTex.SOFT_WOODEN_WALL, MetaTile.class, TileFix.SOFT, Solidity.SOLID, 8, 2, Item.SOFT_WOODEN_WALL),
    WOODEN_SUPPORT(TileTex.WOODEN_SUPPORT, MetaTile.class, TileFix.SOFT, Solidity.SOLID_VERT, 9, 1),
    RASPBERRY_BUSH(TileTex.RASPBERRY_BUSH, MetaTileRaspberryBush.class, TileFix.SOFT, Solidity.LABILE_VERT, 1, 1),
    RASPBERRY_BUSH_GROWN(TileTex.RASPBERRY_BUSH_GROWN, MetaTileRaspberryBush.class, TileFix.SOFT, Solidity.LABILE_VERT, 1, 1),
    COAL_ORE(TileTex.COAL_ORE, MetaTile.class, TileFix.FULL, Solidity.SOLID, 4, 10, Item.COAL),
    SAND(TileTex.SAND, MetaTile.class, TileFix.FULL, Solidity.SOLID_VERT, 1, 3, Item.SAND),
    GLASS(TileTex.GLASS, MetaTile.class, TileFix.FULL, Solidity.SOLID, 4, 1, Item.GLASS),
    CACTUS_PLANT(TileTex.CACTUS_DOUBLE, MetaTileCactus.class, TileFix.SOFT, Solidity.ONLY_VERT, 1, 2, Item.CACTUS),
    CACTUS_DOUBLE(TileTex.CACTUS_DOUBLE, MetaTile.class, TileFix.SOFT, Solidity.ONLY_VERT, 1, 2, Item.CACTUS),
    CACTUS_RIGHT(TileTex.CACTUS_RIGHT, MetaTile.class, TileFix.SOFT, Solidity.ONLY_VERT, 1, 2, Item.CACTUS),
    CACTUS_LEFT(TileTex.CACTUS_LEFT, MetaTile.class, TileFix.SOFT, Solidity.ONLY_VERT, 1, 2, Item.CACTUS),
    CACTUS_TOP(TileTex.CACTUS_TOP, MetaTile.class, TileFix.SOFT, Solidity.ONLY_VERT, 1, 2, Item.CACTUS),
    LEAVES_FIR_TOP(TileTex.LEAVES_FIR_TOP, MetaTileLeaves.class, TileFix.SOFT, Solidity.LABILE_VERT, 1, 1),
    LEAVES_FIR_RIGHT(TileTex.LEAVES_FIR_RIGHT, MetaTileLeaves.class, TileFix.SOFT, Solidity.LABILE, 1, 1),
    LEAVES_FIR_LEFT(TileTex.LEAVES_FIR_LEFT, MetaTileLeaves.class, TileFix.SOFT, Solidity.LABILE, 1, 1),
    LEAVES_FIR_FULL(TileTex.LEAVES_FIR_FULL, MetaTileLeaves.class, TileFix.SOFT, Solidity.SOLID, 3, 1),
    LOG_FIR(TileTex.LOG_FIR, MetaTile.class, TileFix.SOFT, Solidity.ONLY_VERT, 3, 3, Item.RAW_WOOD),
    BLUEBERRY_BUSH(TileTex.BLUEBERRY_BUSH, MetaTileBlueberryBush.class, TileFix.SOFT, Solidity.LABILE_VERT, 1, 1),
    BLUEBERRY_BUSH_GROWN(TileTex.BLUEBERRY_BUSH_GROWN, MetaTileBlueberryBush.class, TileFix.SOFT, Solidity.LABILE_VERT, 1, 1),
    GROWING_PLANT_FIR(TileTex.GROWING_PLANT, MetaTileTreePlant.class, TileFix.SOFT, Solidity.LABILE_VERT, 1, 1),
    GROWING_PLANT_SHOREA(TileTex.GROWING_PLANT, MetaTileTreePlant.class, TileFix.SOFT, Solidity.LABILE_VERT, 1, 1),
    CLAYSTONE(TileTex.CLAYSTONE, MetaTile.class, TileFix.LIMESTONE, Solidity.LABILE_VERT, 1, 3, Item.CLAYSTONE),
    SAND_SLOPE_RIGHT(TileTex.SAND_SLOPE_RIGHT, MetaTile.class, TileFix.SLOPE_R, Solidity.LABILE_VERT, 1, 2, Item.SAND),
    SAND_SLOPE_LEFT(TileTex.SAND_SLOPE_LEFT, MetaTile.class, TileFix.SLOPE_L, Solidity.LABILE_VERT, 1, 2, Item.SAND),
    FURNACE_ON(TileTex.FURNACE_ON, MetaTileFurnace.class, TileFix.FULL, Solidity.LABILE, 3, 4, Item.FURNACE),
    FURNACE_OFF(TileTex.FURNACE_OFF, MetaTileFurnace.class, TileFix.FULL, Solidity.LABILE, 3, 4, Item.FURNACE),
    CHEST(TileTex.CHEST, MetaTileChest.class, TileFix.CHEST, Solidity.LABILE_VERT, 1, 2, Item.CHEST),
    TORCH(TileTex.TORCH, MetaTileTorch.class, TileFix.SOFT, Solidity.LABILE_VERT, 1, 1),
    ;

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

    public MetaTile init(World w, Tile t) {

        // just for optimization
        if(metaClazz == MetaTile.class) {
            return new MetaTile(w, t);
        }else if(metaClazz == MetaTileLeaves.class) {
            return new MetaTileLeaves(w, t);
        }else if(metaClazz == MetaTileRaspberryBush.class) {
            return new MetaTileRaspberryBush(w, t);
        }else if(metaClazz == MetaTileBlueberryBush.class) {
            return new MetaTileBlueberryBush(w, t);
        }
        // end of optimization

        try {
            return metaClazz.getConstructor(World.class, Tile.class).newInstance(w, t);
        }catch(InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return new MetaTile(w, t);
    }

    public MetaTile init(World w, Tile t, DataInputStream in) throws IOException {

        // just for optimization
        if(metaClazz == MetaTile.class) {
            return new MetaTile(w, t);
        }else if(metaClazz == MetaTileLeaves.class) {
            return new MetaTileLeaves(w, t);
        }else if(metaClazz == MetaTileRaspberryBush.class) {
            return new MetaTileRaspberryBush(w, t);
        }else if(metaClazz == MetaTileBlueberryBush.class) {
            return new MetaTileBlueberryBush(w, t);
        }
        // end of optimization

        try {
            return metaClazz.getConstructor(World.class, Tile.class, DataInputStream.class).newInstance(w, t, in);
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
            case CAMPFIRE_BIG:
            case CAMPFIRE_SMALL:
            case GLASS:
            case CLAYSTONE:
            case FURNACE_OFF:
            case FURNACE_ON:
            case CHEST:
            case TORCH:
                return true;
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

    public byte getLight() {
        switch(this) {
            case CAMPFIRE_BIG:
            case FURNACE_ON:
                return 10;
            case TORCH:
                return 8;
            case CAMPFIRE_SMALL: return 5;
            default: return 0;
        }
    }

    public boolean isWatertight() {
        switch(this) {
            case GRASS:
            case DIRT:
            case LIMESTONE:
            case RHYOLITE:
            case COAL_ORE:
            case MAGNETITE:
            case CHROMITE:
            case SAND:
            case GLASS:
            case WOODEN_WALL:
            case SOFT_WOODEN_WALL:
                return true;
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
        map = new HashMap<>();
        int i = 0;
        for(Material tt : Material.values()) {
            tt.id = i;
            map.put(tt.id, tt);
            i++;
        }
    }
}
