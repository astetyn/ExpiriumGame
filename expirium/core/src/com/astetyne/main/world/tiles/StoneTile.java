package com.astetyne.main.world.tiles;

import com.astetyne.main.TextureManager;
import com.astetyne.main.world.Tile;
import com.astetyne.main.world.TileType;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class StoneTile extends Tile {

    public StoneTile() {
        super(TileType.STONE, true);
    }

    @Override
    public TextureRegion getTexture() {
        return TextureManager.STONE_TEXTURE;
    }
}
