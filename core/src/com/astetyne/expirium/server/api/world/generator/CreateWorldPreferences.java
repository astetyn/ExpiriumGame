package com.astetyne.expirium.server.api.world.generator;

public class CreateWorldPreferences extends WorldPreferences {

    public int width, height;
    public int seed;

    public CreateWorldPreferences(String worldName, int width, int height, int seed) {
        super(worldName);
        this.width = width;
        this.height = height;
        this.seed = seed;
    }
}
