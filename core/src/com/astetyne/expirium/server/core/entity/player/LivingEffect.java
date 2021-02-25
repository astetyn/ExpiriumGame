package com.astetyne.expirium.server.core.entity.player;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Locale;

public enum LivingEffect {
    
    DROWNING,
    SERIOUS_DROWNING,
    STARVATION,
    ;

    private TextureRegion tex;

    public static void loadTextures(TextureAtlas atlas) {
        for(LivingEffect effect : values()) {
            String texName = effect.name().toLowerCase(Locale.US) + "_effect";
            effect.tex = atlas.findRegion(texName);
        }
    }

    public static LivingEffect get(int i) {
        return values()[i];
    }

    public TextureRegion getTex() {
        return tex;
    }
}
