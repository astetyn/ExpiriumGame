package com.astetyne.expirium.client.tiles;

import com.astetyne.expirium.client.resources.Textureable;
import com.astetyne.expirium.client.resources.TileTex;
import com.astetyne.expirium.client.resources.TileTexAnim;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.world.tile.ExpiTile;
import com.astetyne.expirium.server.core.world.tile.MetaTile;
import com.astetyne.expirium.server.core.world.tile.meta.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public enum Material implements Textureable {

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

    Material(Textureable textureable, Class<? extends MetaTile> clazz) {
        this.textureable = textureable;
        this.metaClazz = clazz;
    }

    public static Material getMaterial(int id) {
        return map.get(id);
    }

    public int getID() {
        return id;
    }

    public TextureRegion getTex() {
        return textureable.getTex();
    }

    // this method should be used rarely only in edge cases
    public MetaTile init(ExpiServer s) {
        return new ExpiTile(s, this, 0, 0).getMeta();
    }

    public MetaTile init(ExpiServer s, ExpiTile t) {
        try {
            return metaClazz.getConstructor(ExpiServer.class, ExpiTile.class).newInstance(s, t);
        }catch(InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return new MetaTileAir(s, t);
    }

    public MetaTile init(ExpiServer s, ExpiTile t, DataInputStream in) throws IOException {
        try {
            return metaClazz.getConstructor(ExpiServer.class, ExpiTile.class, DataInputStream.class)
                    .newInstance(s, t, in);
        }catch(InstantiationException | NoSuchMethodException | IllegalAccessException e) {
            return init(s, t);
        }catch(InvocationTargetException e) {
            throw (IOException) e.getTargetException(); // idk if this is correct
        }
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
