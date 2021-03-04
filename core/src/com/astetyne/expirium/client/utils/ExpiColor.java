package com.astetyne.expirium.client.utils;

import com.badlogic.gdx.graphics.Color;

public enum ExpiColor {

    RED(Color.RED),
    GREEN(Color.GREEN),
    ORANGE(Color.ORANGE),
    WHITE(Color.WHITE),
    ;

    private final Color c;

    ExpiColor(Color c) {
        this.c = c;
    }

    public Color getColor() {
        return c;
    }

    public static ExpiColor get(int i) {
        return values()[i];
    }

}
