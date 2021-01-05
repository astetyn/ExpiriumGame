package com.astetyne.expirium.main.world;

import com.badlogic.gdx.math.Vector2;

public class LightSource {

    private final LightType type;
    private final Vector2 loc;

    public LightSource(LightType type, Vector2 loc) {
        this.type = type;
        this.loc = loc;
    }

    public LightType getType() {
        return type;
    }

    public Vector2 getLoc() {
        return loc;
    }
}
