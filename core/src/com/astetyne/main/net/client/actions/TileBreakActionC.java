package com.astetyne.main.net.client.actions;

import com.astetyne.main.world.tiles.Tile;

public class TileBreakActionC extends ClientAction {

    private final int chunkID, x, y;

    public TileBreakActionC(Tile t) {
        this.chunkID = t.getChunk().getId();
        this.x = t.getX();
        this.y = t.getY();
    }

    public int getChunkID() {
        return chunkID;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
