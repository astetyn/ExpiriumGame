package com.astetyne.expirium.server.api.world.tiles;

import com.astetyne.expirium.client.tiles.TileType;
import com.badlogic.gdx.physics.box2d.Fixture;

import java.util.ArrayList;
import java.util.List;

public class ExpiTile {

    private TileType typeFront, typeBack;
    private final List<Fixture> fixtures;
    private final int x, y;
    private int stability;

    public ExpiTile(TileType typeFront, TileType typeBack, int x, int y) {
        this.typeFront = typeFront;
        this.typeBack = typeBack;
        fixtures = new ArrayList<>();
        this.x = x;
        this.y = y;
        stability = 0;
    }

    public void setTypeFront(TileType type) {
        this.typeFront = type;
    }

    public TileType getTypeFront() {
        return typeFront;
    }

    public TileType getTypeBack() {
        return typeBack;
    }

    public void setTypeBack(TileType typeBack) {
        this.typeBack = typeBack;
    }

    public void setStability(int stability) {
        this.stability = stability;
    }

    public int getStability() {
        return stability;
    }

    public boolean isLabile() {
        return typeFront.getSolidity().isLabile();
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
