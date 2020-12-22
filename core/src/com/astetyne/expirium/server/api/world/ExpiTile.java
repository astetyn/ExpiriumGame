package com.astetyne.expirium.server.api.world;

import com.astetyne.expirium.main.world.tiles.TileType;
import com.badlogic.gdx.physics.box2d.Fixture;

import java.util.ArrayList;
import java.util.List;

public class ExpiTile {

    private TileType type;
    private final List<Fixture> fixtures;
    private final int x, y;
    private int stability;
    //todo: meta?

    public ExpiTile(TileType type, int x, int y) {
        this.type = type;
        fixtures = new ArrayList<>();
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
