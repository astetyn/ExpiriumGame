package com.astetyne.expirium.client.utils;

import com.badlogic.gdx.graphics.Color;

import java.util.HashMap;

public enum ExpiColor {

    RED(Color.RED),
    GREEN(Color.GREEN),
    ORANGE(Color.ORANGE),
    ;

    private final Color c;

    ExpiColor(Color c) {
        this.c = c;
    }

    public Color getColor() {
        return c;
    }

    public byte getId() {
        return id;
    }

    byte id;
    private static final HashMap<Byte, ExpiColor> map;
    static {
        System.out.println("ExpiColor class loading.");
        map = new HashMap<>();
        byte i = 0;
        for(ExpiColor it : ExpiColor.values()) {
            it.id = i;
            map.put(it.id, it);
            i++;
        }
    }

    public static ExpiColor get(int id) {
        return map.get((byte)id);
    }

}
