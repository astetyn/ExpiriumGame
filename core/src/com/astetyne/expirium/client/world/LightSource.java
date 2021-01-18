package com.astetyne.expirium.client.world;

import com.astetyne.expirium.client.utils.IntVector2;

public class LightSource {

    private final byte radius;
    private final IntVector2 loc;

    public LightSource(byte radius, IntVector2 loc) {
        this.radius = radius;
        this.loc = loc;
    }

    public byte getRadius() {
        return radius;
    }

    public IntVector2 getLoc() {
        return loc;
    }
}
