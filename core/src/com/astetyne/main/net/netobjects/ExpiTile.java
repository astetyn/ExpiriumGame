package com.astetyne.main.net.netobjects;

import com.astetyne.main.world.TileType;
import com.badlogic.gdx.physics.box2d.Fixture;

import java.util.ArrayList;
import java.util.List;

public class ExpiTile {

    private TileType type;
    private final List<Fixture> fixtures;
    private final ExpiChunk chunk;
    private final int x, y;
    private int stability;

    public ExpiTile(TileType type, ExpiChunk chunk, int x, int y) {
        this.type = type;
        fixtures = new ArrayList<>();
        this.chunk = chunk;
        this.x = x;
        this.y = y;
        stability = 0;
    }

    public void setType(TileType type) {
        this.type = type;
    }

    public TileType getType() {
        return type;
    }

    public void setStability(int stability) {
        this.stability = stability;
    }

    public int getStability() {
        return stability;
    }

    public List<Fixture> getFixtures() {
        return fixtures;
    }

    public ExpiChunk getChunk() {
        return chunk;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
