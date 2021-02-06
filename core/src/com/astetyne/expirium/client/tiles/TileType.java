package com.astetyne.expirium.client.tiles;

import com.astetyne.expirium.client.resources.Textureable;
import com.astetyne.expirium.client.resources.TileTex;
import com.astetyne.expirium.client.resources.TileTexAnim;
import com.astetyne.expirium.server.core.world.tile.MetaTile;
import com.astetyne.expirium.server.core.world.tile.meta.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public enum TileType implements Textureable {

    AIR(null, MetaTileAir.class),
    STONE(TileTex.STONE, MetaTileStone.class),
    RHYOLITE(TileTex.RHYOLITE, MetaTileRhyolite.class),
    GRASS(TileTex.GRASS, MetaTileGrass.class),
    GRASS_SLOPE_R(TileTex.GRASS_SLOPE_R, MetaTileGrassSlopeR.class),
    GRASS_SLOPE_L(TileTex.GRASS_SLOPE_L, MetaTileGrassSlopeL.class),
    DIRT(TileTex.DIRT, MetaTileDirt.class),
    LOG1(TileTex.TREE1, MetaTileLog.class),
    LOG2(TileTex.TREE2, MetaTileLog.class),
    LOG3(TileTex.TREE3, MetaTileLog.class),
    LEAVES1(TileTex.TREE4, MetaTileLeaves.class),
    LEAVES2(TileTex.TREE5, MetaTileLeaves.class),
    LEAVES3(TileTex.TREE6, MetaTileLeaves.class),
    CAMPFIRE_SMALL(TileTexAnim.CAMPFIRE_SMALL, MetaTileCampfire.class),
    CAMPFIRE_BIG(TileTexAnim.CAMPFIRE_BIG, MetaTileCampfire.class),
    WOODEN_WALL(TileTex.WOODEN_WALL, MetaTileWoodenWall.class),
    SOFT_WOODEN_WALL(TileTex.SOFT_WOODEN_WALL, MetaTileSoftWoodenWall.class),
    WOODEN_SUPPORT(TileTex.WOODEN_SUPPORT, MetaTileWoodenSupport.class),
    RASPBERRY_BUSH_1(TileTex.RASPBERRY_BUSH_1, MetaTileRaspberryBush.class),
    RASPBERRY_BUSH_2(TileTex.RASPBERRY_BUSH_2, MetaTileRaspberryBush.class),
    COAL_ORE(TileTex.COAL_ORE, MetaTileCoalOre.class),
    SAND(TileTex.SAND, MetaTileSand.class),
    GLASS(TileTex.GLASS, MetaTileGlass.class);

    Textureable textureable;
    Class<? extends MetaTile> metaClazz;

    TileType(Textureable textureable, Class<? extends MetaTile> clazz) {
        this.textureable = textureable;
        this.metaClazz = clazz;
    }

    public static TileType getType(int id) {
        return map.get(id);
    }

    public int getID() {
        return id;
    }

    public TextureRegion getTex() {
        return textureable.getTex();
    }

    public Class<? extends MetaTile> getMetaClazz() {
        return metaClazz;
    }

    int id;
    private static final HashMap<Integer, TileType> map;
    static {
        System.out.println("TileType class loading.");
        map = new HashMap<>();
        int i = 0;
        for(TileType tt : TileType.values()) {
            tt.id = i;
            map.put(tt.id, tt);
            i++;
        }
    }
}
