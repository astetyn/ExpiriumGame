package com.astetyne.expirium.client.world;

import com.astetyne.expirium.client.Res;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

public enum LightType {

    TRANSP_SPHERE_MEDIUM(15, 15, new Color(1,1,1,1), Res.LIGHT_SPH_1.getTexture());

    float width, height;
    Color color;
    Texture texture;

    LightType(float width, float height, Color color, Texture texture) {
        this.width = width;
        this.height = height;
        this.color = color;
        this.texture = texture;
    }
}
