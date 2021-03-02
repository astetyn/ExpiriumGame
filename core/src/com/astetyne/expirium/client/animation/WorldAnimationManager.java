package com.astetyne.expirium.client.animation;

import com.astetyne.expirium.client.resources.Res;
import com.astetyne.expirium.client.utils.ExpiColor;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WorldAnimationManager {

    private final static long textDecayTime = 2000;
    private final static float decayHeight = 1;

    private final List<TextAnimation> textAnimations;

    public WorldAnimationManager() {
        textAnimations = new ArrayList<>();
    }

    public void update() {
        Iterator<TextAnimation> it = textAnimations.iterator();
        while(it.hasNext()) {
            TextAnimation ta = it.next();
            if(ta.createTime + textDecayTime < System.currentTimeMillis()) it.remove();
            float f = (System.currentTimeMillis() - ta.createTime) / (float)(textDecayTime);
            ta.loc.set(ta.origLoc.x, ta.origLoc.y + decayHeight * f);
            ta.tempColor.a = ta.createTime + textDecayTime/2 > System.currentTimeMillis() ? 1 : 1-f*2;
        }
    }

    public void draw(SpriteBatch batch) {
        for(TextAnimation ta : textAnimations) {
            Res.WORLD_FONT.setColor(ta.tempColor);
            Res.WORLD_FONT.draw(batch, ta.text, ta.loc.x, ta.loc.y);
            Res.WORLD_FONT.setColor(Color.WHITE);
        }
    }

    public void onPlayTextAnimation(PacketInputStream in) {
        Vector2 loc = in.getVector();
        String text = in.getString();
        ExpiColor c = ExpiColor.get(in.getByte());
        textAnimations.add(new TextAnimation(loc, text, c));
    }
}
