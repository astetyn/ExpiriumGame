package com.astetyne.main.world.tiles;

import com.astetyne.main.world.TileType;
import com.astetyne.main.world.WorldChunk;
import com.astetyne.main.world.tiles.data.AirExtraData;
import com.astetyne.main.world.tiles.data.TileExtraData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Fixture;

import java.util.ArrayList;
import java.util.List;

public class Tile {

    private final List<Fixture> fixtures;
    private final WorldChunk chunk;
    private final int x, y;
    private TileExtraData tileExtraData;
    private TileType type;

    public Tile(WorldChunk chunk, int x, int y) {
        this.fixtures = new ArrayList<>();
        this.chunk = chunk;
        this.x = x;
        this.y = y;
        tileExtraData = new AirExtraData();
        type = tileExtraData.getType();
    }

    public TileType getType() {
        return type;
    }

    public List<Fixture> getFixtures() {
        return fixtures;
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

    public void setTileExtraData(TileExtraData tileExtraData) {
        this.tileExtraData = tileExtraData;
        type = tileExtraData.getType();
    }

    public TileExtraData getTileExtraData() {
        return tileExtraData;
    }

    public boolean isSolid() {
        return tileExtraData.isSolid();
    }

    public TextureRegion getTexture() {
        return tileExtraData.getTexture();
    }

    public void destroy() {
        chunk.destroyTile(this);
    }
}
