package com.astetyne.expirium.client.resources;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public enum TileTex implements Textureable {

    STONE("stone_tile"),
    RHYOLITE("rhyolite_tile"),
    GRASS("grass"),
    GRASS_SLOPE_R("grass_slope_right"),
    GRASS_SLOPE_L("grass_slope_left"),
    DIRT("dirt_tile"),
    TREE1("tree1"),
    TREE2("tree2"),
    TREE3("tree3"),
    TREE4("tree4"),
    TREE5("tree5"),
    TREE6("tree6"),
    WOODEN_WALL("wooden_wall"),
    SOFT_WOODEN_WALL("soft_wooden_wall"),
    WOODEN_SUPPORT("wooden_support"),
    WHITE_TILE("white_tile"),
    COAL_ORE("coal_ore_tile"),
    RASPBERRY_BUSH_1("raspberry_bush_tile"),
    RASPBERRY_BUSH_2("raspberry_bush_2_tile");

    private final String regionName;
    private TextureRegion textureRegion;

    TileTex(String regionName) {
        this.regionName = regionName;
    }

    public static void loadTextures() {
        System.out.println("Loading textures for tiles.");
        TextureAtlas world = new TextureAtlas("world.atlas");
        for(TileTex tileTex : values()) {
            tileTex.textureRegion = world.findRegion(tileTex.regionName);
            //tileTex.textureRegion.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
    }

    public TextureRegion getTex() {
        return textureRegion;
    }
}
