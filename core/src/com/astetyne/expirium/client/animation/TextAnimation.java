package com.astetyne.expirium.client.animation;

import com.astetyne.expirium.client.utils.ExpiColor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class TextAnimation {

    public final Vector2 origLoc, loc;
    public final String text;
    public final Color tempColor;
    public final long createTime;

    public TextAnimation(Vector2 loc, String text, ExpiColor baseColor) {
        this.origLoc = new Vector2(loc);
        this.loc = loc;
        this.text = text;
        tempColor = new Color(baseColor.getColor());
        createTime = System.currentTimeMillis();
    }
}
