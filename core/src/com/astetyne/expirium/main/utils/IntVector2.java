package com.astetyne.expirium.main.utils;

public class IntVector2 {

    public int x, y;

    public IntVector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void set(IntVector2 vec) {
        this.x = vec.x;
        this.y = vec.y;
    }

    @Override
    public String toString() {
        return "x: "+x+" y: "+y;
    }
}