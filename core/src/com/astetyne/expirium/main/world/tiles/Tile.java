package com.astetyne.expirium.main.world.tiles;

import com.astetyne.expirium.main.world.LightSource;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashSet;

public class Tile {

    private int c, x, y;
    private int stability;
    private TileType type;
    private final HashSet<LightSource> attachedLights;

    public Tile() {
        this.c = 0;
        this.x = 0;
        this.y = 0;
        this.stability = 0;
        this.type = TileType.AIR;
        attachedLights = new HashSet<>();
    }

    public void init(int c, int x, int y, int stability, TileType type) {
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

    public HashSet<LightSource> getAttachedLights() {
        return attachedLights;
    }
}
