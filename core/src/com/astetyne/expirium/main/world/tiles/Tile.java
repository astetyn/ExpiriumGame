package com.astetyne.expirium.main.world.tiles;

import com.astetyne.expirium.main.world.LightSource;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashSet;

public class Tile {

    private TileType type;
    private final int x, y;
    private int stability;
    private final HashSet<LightSource> attachedLights;

    public Tile(TileType type, int x, int y, int stability) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.stability = stability;
        attachedLights = new HashSet<>();
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
