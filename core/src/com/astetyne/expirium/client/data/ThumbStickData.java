package com.astetyne.expirium.client.data;

public class ThumbStickData {

    public float vert, horz;

    public ThumbStickData() {
        this.vert = 0;
        this.horz = 0;
    }

    public void reset() {
        vert = 0;
        horz = 0;
    }

    public boolean isNeutral() {
        return vert == 0 && horz == 0;
    }
}
