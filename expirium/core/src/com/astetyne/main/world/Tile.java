package com.astetyne.main.world;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Fixture;

import java.util.ArrayList;
import java.util.List;

public abstract class Tile {

    private final boolean solid;
    private final TileType type;
    private final List<Fixture> fixtures;

    public Tile(TileType type, boolean solid) {
        this.solid = solid;
        this.type = type;
        this.fixtures = new ArrayList<>();
    }

    public TileType getType() {
        return type;
    }

    public List<Fixture> getFixtures() {
        return fixtures;
    }

    public abstract TextureRegion getTexture();

    public boolean isSolid() {
        return solid;
    }
}
