package com.astetyne.expirium.client.gui.widget;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import java.util.LinkedList;
import java.util.Queue;

public class WarnMsgLabel extends Label {

    private final Queue<WarningMessage> queue;
    private long lastMsgStart;
    private WarningMessage actualMsg;

    public WarnMsgLabel(LabelStyle style) {
        super("", style);
        setAlignment(Align.center);
        setTouchable(Touchable.disabled);
        queue = new LinkedList<>();
        lastMsgStart = 0;
    }

    public void addWarning(String msg, long duration, Color color) {
        queue.add(new WarningMessage(msg, duration, color));
    }

    public void act(float delta) {

        if(actualMsg != null && lastMsgStart + actualMsg.duration < System.currentTimeMillis()) {
            actualMsg = null;
            setText("");
        }

        if(queue.size() == 0) return;

        if(actualMsg == null) {
            actualMsg = queue.poll();
            lastMsgStart = System.currentTimeMillis();
            setColor(actualMsg.color);
            setText(actualMsg.msg);
        }

    }

    public static class WarningMessage {

        public String msg;
        public long duration;
        public Color color;

        public WarningMessage(String msg, long duration, Color color) {
            this.msg = msg;
            this.duration = duration;
            this.color = color;
        }
    }

}
