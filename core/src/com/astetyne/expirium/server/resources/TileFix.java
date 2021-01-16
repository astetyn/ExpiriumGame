package com.astetyne.expirium.server.resources;

public enum TileFix {

    CAMPFIRE(new float[]{0.2f, 0, 0.8f, 0, 0.8f, 0.3f, 0.2f, 0.3f, 0.2f, 0}),
    GRASS_SLOPE_L(new float[]{0, 0, 1, 0, 0, 1, 0, 0}),
    GRASS_SLOPE_R(new float[]{0, 0, 1, 0, 1, 1, 0, 0});

    private final float[] vertices;

    TileFix(float[] vertices) {
        this.vertices = vertices;
    }

    public float[] getVertices() {
        return vertices;
    }
}
