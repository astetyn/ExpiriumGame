package com.astetyne.expirium.client.resources;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public enum BGRes {

    BACKGROUND_1("bg1", false),
    BACKGROUND_2("bg2", false),
    BACKGROUND_3("bg3", false),
    SUN("sun", false),
    MOON("moon", false);

    private final String name;
    private final boolean ninepatch;
    private Drawable drawable;

    BGRes(String drawableName, boolean ninepatch) {
        this.name = drawableName;
        this.ninepatch = ninepatch;
    }

    public static void loadTextures() {
        TextureAtlas gui = new TextureAtlas("background.atlas");
        for(BGRes val : values()) {
            if(val.ninepatch) {
                val.drawable = new NinePatchDrawable(gui.createPatch(val.name));
            }else {
                val.drawable = new TextureRegionDrawable(gui.findRegion(val.name));
            }
        }
    }

    public Drawable getDrawable() {
        return drawable;
    }

}
