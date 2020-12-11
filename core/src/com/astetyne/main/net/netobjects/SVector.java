package com.astetyne.main.net.netobjects;

import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;

public class SVector implements Serializable {

    private final float x, y;

    public SVector(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public SVector(Vector2 location) {
        this.x = location.x;
        this.y = location.y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Vector2 toVector() {
        return new Vector2(x, y);
    }
}
