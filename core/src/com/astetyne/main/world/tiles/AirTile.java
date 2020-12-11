package com.astetyne.main.world.tiles;

import com.astetyne.main.world.Tile;
import com.astetyne.main.world.TileType;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AirTile extends Tile {

    public AirTile() {
        super(TileType.AIR, false);
    }

    @Override
    public TextureRegion getTexture() {
        return null;
    }
}
