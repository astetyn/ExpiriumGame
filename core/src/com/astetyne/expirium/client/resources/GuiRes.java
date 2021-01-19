package com.astetyne.expirium.client.resources;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public enum GuiRes {

    DEBUG("debug_icon", false),
    FRAME_GRAY("frame_gray", true),
    FRAME_GREEN("frame_green", true),
    FRAME_YELLOW("frame_yellow", true),
    FRAME_SQUARE("frame_square", true),
    FRAME_GRAY_TRANSP("frame_gray_trans", true),
    INV("inventory", false),
    INV_WEIGHT("weight_icon", false),
    HEALTH_ICON("health_icon", false),
    FOOD_ICON("food_icon", false),
    TEMP_ICON("temp_icon", false),
    TRASH_ICON("trash_icon", false),
    SETTINGS_ICON("settings_icon", false),
    USE_ICON("use_icon", false),
    WARNING_ICON("warning_icon", false);

    private final String name;
    private final boolean ninepatch;
    private Drawable drawable;

    GuiRes(String drawableName, boolean ninepatch) {
        this.name = drawableName;
        this.ninepatch = ninepatch;
    }

    public static void loadTextures() {
        TextureAtlas gui = new TextureAtlas("gui.atlas");
        for(GuiRes val : values()) {
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
