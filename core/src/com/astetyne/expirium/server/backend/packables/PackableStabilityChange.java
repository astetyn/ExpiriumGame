package com.astetyne.expirium.server.backend.packables;

import com.astetyne.expirium.main.utils.Constants;
import com.astetyne.expirium.server.api.world.ExpiTile;

import java.nio.ByteBuffer;

public class PackableStabilityChange {

    private final int c, x, y;
    private final int stability;

    public PackableStabilityChange(ExpiTile t) {
        this.c = t.getX() / Constants.T_W_CH;
        this.x = t.getX() - c*Constants.T_W_CH;
        this.y = t.getY();
        this.stability = t.getStability();
    }

    public void populateWithData(ByteBuffer bb) {
        bb.putInt(c);
        bb.putInt(x);
        bb.putInt(y);
        bb.putInt(stability);
    }
}
