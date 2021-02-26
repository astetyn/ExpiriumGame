package com.astetyne.expirium.client.gui.widget;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

public class WarnMsgLabel extends Label {

    private final static int transparentTime = 200;

    private long startTime;
    public int duration;

    public WarnMsgLabel(LabelStyle style) {
        super("", style);
        setAlignment(Align.center);
        setTouchable(Touchable.disabled);
        startTime = 0;
        duration = 0;
    }

    public void setMsg(String msg, int durationMillis, Color color) {
        setText(msg);
        duration = durationMillis;
        setColor(color);
        startTime = System.currentTimeMillis();
        setVisible(true);
    }

    public void act(float delta) {

        int diff = (int) (startTime + duration - System.currentTimeMillis());

        if(diff < 0) {
            setVisible(false);
        }else if(diff < transparentTime) {
            getColor().a = (float)diff / transparentTime;
        }
    }

}
