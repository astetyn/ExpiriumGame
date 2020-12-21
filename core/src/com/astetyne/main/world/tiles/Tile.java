package com.astetyne.main.world.tiles;

import com.astetyne.main.world.WorldChunk;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Tile {

    private final WorldChunk chunk;
    private final int x, y;
    private int stability;
    private TileType type;

    public Tile(WorldChunk chunk, int x, int y, TileType type) {
        this.chunk = chunk;
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public void setType(TileType type) {
        this.type = type;
    }

    public TileType getType() {
        return type;
    }

    public WorldChunk getChunk() {
        return chunk;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Vector2 getCenterLoc() {
        return new Vector2(x + 0.5f, y + 0.5f);
    }

    public boolean isSolid() {
        return type.isSolid();
    }

    public TextureRegion getTexture() {
        return type.getTexture();
    }

    public int getStability() {
        return stability;
    }

    public void setStability(int stability) {
        this.stability = stability;
    }
}
