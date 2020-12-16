package com.astetyne.main.items;

import com.astetyne.main.Resources;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ItemFactory {

    public static TextureRegion getTexture(ItemType type) {

        switch(type) {

            case STONE: return Resources.STONE_TEXTURE;

        }
        return null;
    }

}
