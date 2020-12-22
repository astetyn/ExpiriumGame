package com.astetyne.expirium.server.backend.packables;

import com.astetyne.expirium.main.utils.Constants;
import com.astetyne.expirium.server.api.world.ExpiTile;

import java.nio.ByteBuffer;

public class PackableBrokenTile {

    private final int c, x, y;

    public PackableBrokenTile(ExpiTile t) {
        this.c = t.getX() / Constants.T_W_CH;
        this.x = t.getX() - c*Constants.T_W_CH;
        this.y = t.getY();
    }

    public void populateWithData(ByteBuffer bb) {
        bb.putInt(c);
        bb.putInt(x);
        bb.putInt(y);
    }

}
