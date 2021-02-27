package com.astetyne.expirium.client.resources;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public enum GuiRes {

    DEBUG("debug_icon"),
    INV("inventory"),
    INV_WEIGHT("weight_icon"),
    HEALTH_ICON("health_icon"),
    FOOD_ICON("food_icon"),
    TEMP_ICON("temp_icon"),
    TRASH_ICON("trash_icon"),
    SETTINGS_ICON("settings_icon"),
    USE_ICON("use_icon"),
    WARNING_ICON("warning_icon");

    private final String name;
    private TextureRegionDrawable drawable;

    GuiRes(String drawableName) {
        this.name = drawableName;
    }

    public static void loadTextures(TextureAtlas atlas) {
        for(GuiRes val : values()) {
            val.drawable = new TextureRegionDrawable(atlas.findRegion(val.name));
        }
    }

    public TextureRegionDrawable getDrawable() {
        return drawable;
    }
}
