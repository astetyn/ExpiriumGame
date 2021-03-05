package com.astetyne.expirium.client.data;

import com.astetyne.expirium.client.utils.ExpiColor;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Locale;

public enum InvVariableType {

    STOPWATCH(ExpiColor.WHITE),
    COAL_FUEL(ExpiColor.WHITE),
    RECYCLE(ExpiColor.WHITE),
    ;

    private TextureRegion iconTex;
    private final ExpiColor color;

    InvVariableType(ExpiColor color) {
        this.color = color;
    }

    public static void loadTextures(TextureAtlas atlas) {
        for(InvVariableType type : values()) {
            type.iconTex = atlas.findRegion(type.name().toLowerCase(Locale.US)+"_icon");
        }
    }

    public static InvVariableType get(int i) {
        return values()[i];
    }

    public TextureRegion getIconTex() {
        return iconTex;
    }

    public ExpiColor getColor() {
        return color;
    }
}
