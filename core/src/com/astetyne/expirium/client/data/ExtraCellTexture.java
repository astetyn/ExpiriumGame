package com.astetyne.expirium.client.data;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Locale;

public enum ExtraCellTexture {

    SPLIT_HALF,
    SPLIT_ONE,
    TRASH,
    COAL_FUEL,
    ;

    private TextureRegion tex;

    public static void loadTextures(TextureAtlas atlas) {
        for(ExtraCellTexture item : values()) {
            item.tex = atlas.findRegion(item.name().toLowerCase(Locale.US)+"_cell");
        }
    }

    public static ExtraCellTexture get(int i) {
        return values()[i];
    }

    public TextureRegion getTex() {
        return tex;
    }
}
