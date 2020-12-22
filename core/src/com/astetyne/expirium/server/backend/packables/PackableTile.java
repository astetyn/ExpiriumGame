package com.astetyne.expirium.server.backend.packables;

import java.nio.ByteBuffer;

public class PackableTile {

    private final byte tileType;
    private final byte stability;

    public PackableTile(byte tileType, byte stability) {
        this.tileType = tileType;
        this.stability = stability;
    }

    public void populateWithData(ByteBuffer bb) {
        bb.put(tileType);
        bb.put(stability);
    }
}
