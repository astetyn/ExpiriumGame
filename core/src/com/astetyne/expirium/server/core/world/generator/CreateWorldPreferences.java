package com.astetyne.expirium.server.core.world.generator;

public class CreateWorldPreferences extends WorldPreferences {

    public int width, height;
    public long seed;

    public CreateWorldPreferences(String worldName, int width, int height, long seed) {
        super(worldName);
        this.width = width;
        this.height = height;
        this.seed = seed;
    }
}
