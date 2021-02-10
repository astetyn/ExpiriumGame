package com.astetyne.expirium.server.core.world.tile;

public enum TileFix {

    FULL(null, 0.2f),
    SOFT(null, 0),
    CAMPFIRE(new float[]{0.2f, 0, 0.8f, 0, 0.8f, 0.3f, 0.2f, 0.3f, 0.2f, 0}, 0.3f),
    GRASS_SLOPE_L(new float[]{0, 0, 1, 0, 0, 1, 0, 0}, 0.5f),
    GRASS_SLOPE_R(new float[]{0, 0, 1, 0, 1, 1, 0, 0}, 0.5f);

    private final float[] vertices;
    private final float friction;

    TileFix(float[] vertices, float friction) {
        this.vertices = vertices;
        this.friction = friction;
    }

    public float[] getVertices() {
        return vertices;
    }

    public float getFriction() {
        return friction;
    }
}