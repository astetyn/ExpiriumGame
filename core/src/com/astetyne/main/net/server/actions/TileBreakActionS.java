package com.astetyne.main.net.server.actions;

public class TileBreakActionS extends ServerAction {

    private final int chunkID, x, y;

    public TileBreakActionS(int chunkID, int x, int y) {
        this.chunkID = chunkID;
        this.x = x;
        this.y = y;
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
