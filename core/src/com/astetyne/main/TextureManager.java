package com.astetyne.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class TextureManager {

    public static TextureRegion STONE_TEXTURE;

    public static TextureRegion THUMB_STICK_UP;
    public static TextureRegion THUMB_STICK_DOWN;

    public static TextureAtlas atlasTiles;
    public static TextureAtlas atlasGui;

    public static Skin DEFAULT_SKIN;

    public static void loadTextures() {
        atlasTiles = new TextureAtlas("tiles.atlas");
        atlasGui = new TextureAtlas("gui.atlas");

        STONE_TEXTURE = atlasTiles.findRegion("stone");

        THUMB_STICK_UP = atlasGui.findRegion("thumb_stick_up");
        THUMB_STICK_DOWN = atlasGui.findRegion("thumb_stick_down");

        DEFAULT_SKIN = new Skin(Gdx.files.internal("skin/uiskin.json"));
    }

}
