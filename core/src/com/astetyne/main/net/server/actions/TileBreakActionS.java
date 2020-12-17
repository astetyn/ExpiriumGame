package com.astetyne.main.net.server.actions;

public class TileBreakActionS extends ServerAction {

    private final int chunkID, x, y;
    private final float itemAngleVel;
    private final int itemDropID;

    public TileBreakActionS(int chunkID, int x, int y, int itemDropID) {
        this.chunkID = chunkID;
        this.x = x;
        this.y = y;
        itemAngleVel = ((float)Math.random()-0.5f)*10;
        this.itemDropID = itemDropID;
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

    public float getItemAngleVel() {
        return itemAngleVel;
    }

    public int getItemDropID() {
        return itemDropID;
    }
}
