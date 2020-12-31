package com.astetyne.expirium.main.world.tiles;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Tile {

    private final int c, x, y;
    private int stability;
    private TileType type;

    public Tile(int c, int x, int y, int stability, TileType type) {
        this.c = c;
        this.x = x;
        this.y = y;
        this.stability = stability;
        this.type = type;
    }

    public int getC() {
        return c;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getStability() {
        return stability;
    }

    public void setStability(int stability) {
        this.stability = stability;
    }

    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
    }

    public TextureRegion getTex() {
        return type.getTexture();
    }
}
