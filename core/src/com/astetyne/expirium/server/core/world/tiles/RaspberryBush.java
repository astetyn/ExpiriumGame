package com.astetyne.expirium.server.core.world.tiles;

public class RaspberryBush {

    private final ExpiTile tile;
    private float growTime;

    public RaspberryBush(ExpiTile tile, float growTime) {
        this.tile = tile;
        this.growTime = growTime;
    }

    public ExpiTile getTile() {
        return tile;
    }

    public float getGrowTime() {
        return growTime;
    }

    public void decreaseGrowTime(float f) {
        growTime -= f;
    }
}
