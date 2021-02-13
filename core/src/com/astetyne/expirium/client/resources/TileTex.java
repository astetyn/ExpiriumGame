package com.astetyne.expirium.client.resources;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Locale;

public enum TileTex implements Textureable {

    STONE,
    RHYOLITE,
    GRASS,
    GRASS_SLOPE_RIGHT,
    GRASS_SLOPE_LEFT,
    DIRT,
    LOG_SHOREA,
    LOG_SHOREA_RIGHT,
    LOG_SHOREA_LEFT,
    LEAVES_SHOREA_TOP,
    LEAVES_SHOREA_RIGHT,
    LEAVES_SHOREA_LEFT,
    WOODEN_WALL,
    SOFT_WOODEN_WALL,
    WOODEN_SUPPORT,
    WHITE_TILE,
    COAL_ORE,
    RASPBERRY_BUSH,
    RASPBERRY_BUSH_GROWN,
    SAND,
    GLASS,
    BACK_WALL,
    CACTUS_DOUBLE,
    CACTUS_TOP,
    CACTUS_RIGHT,
    CACTUS_LEFT,
    LIMESTONE,
    LOG_FIR,
    GROWING_PLANT,
    BLUEBERRY_BUSH,
    BLUEBERRY_BUSH_GROWN,
    LEAVES_FIR_RIGHT,
    LEAVES_FIR_LEFT,
    LEAVES_FIR_TOP,
    LEAVES_FIR_FULL,
    SAND_SLOPE_RIGHT,
    SAND_SLOPE_LEFT
    ;

    private TextureRegion textureRegion;

    public static void loadTextures(TextureAtlas world) {
        System.out.println("Loading textures for tiles.");
        for(TileTex tileTex : values()) {
            if(tileTex == LEAVES_SHOREA_LEFT) {
                tileTex.textureRegion = new TextureRegion(world.findRegion(LEAVES_SHOREA_RIGHT.name().toLowerCase(Locale.US)));
                tileTex.textureRegion.flip(true, false);
            }else if(tileTex == LEAVES_FIR_LEFT) {
                tileTex.textureRegion = new TextureRegion(world.findRegion(LEAVES_FIR_RIGHT.name().toLowerCase(Locale.US)));
                tileTex.textureRegion.flip(true, false);
            }else if(tileTex == GRASS_SLOPE_LEFT) {
                tileTex.textureRegion = new TextureRegion(world.findRegion(GRASS_SLOPE_RIGHT.name().toLowerCase(Locale.US)));
                tileTex.textureRegion.flip(true, false);
            }else if(tileTex == SAND_SLOPE_LEFT) {
                tileTex.textureRegion = new TextureRegion(world.findRegion(SAND_SLOPE_RIGHT.name().toLowerCase(Locale.US)));
                tileTex.textureRegion.flip(true, false);
            }else {
                tileTex.textureRegion = world.findRegion(tileTex.name().toLowerCase(Locale.US));
            }
            System.out.println("loaded texture: "+tileTex.textureRegion+" for: "+tileTex);
            //tileTex.textureRegion.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
    }

    public TextureRegion getTex() {
        return textureRegion;
    }
}
