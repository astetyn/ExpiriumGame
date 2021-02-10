package com.astetyne.expirium.server.core.world.file;

public class WorldQuickInfo {

    public long tick;
    public boolean firstLife;
    public int worldVersion;

    public WorldQuickInfo(long tick, boolean firstLife, int worldVersion) {
        this.tick = tick;
        this.firstLife = firstLife;
        this.worldVersion = worldVersion;
    }
}
