package com.astetyne.expirium.server.core.world.tiles;

import com.astetyne.expirium.client.tiles.TileType;
import com.astetyne.expirium.server.core.Saveable;
import com.badlogic.gdx.physics.box2d.Fixture;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExpiTile implements Saveable {

    private TileType type;
    private final List<Fixture> fixtures;
    private final int x, y;
    private int stability;
    private boolean backWall;

    public ExpiTile(TileType type, int x, int y) {
        this.type = type;
        fixtures = new ArrayList<>();
        this.x = x;
        this.y = y;
        stability = 0;
        backWall = false;
    }

    public ExpiTile(DataInputStream in, int x, int y) throws IOException {
        this.type = TileType.getType(in.readByte());
        fixtures = new ArrayList<>();
        this.x = x;
        this.y = y;
        stability = 0;
        backWall = in.readBoolean();
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

    public boolean isLabile() {
        return type.getSolidity().isLabile();
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

    public boolean hasBackWall() {
        return backWall;
    }

    public void setBackWall(boolean backWall) {
        this.backWall = backWall;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(type.getID());
        out.writeBoolean(backWall);
    }
}
