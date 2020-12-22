package com.astetyne.expirium.main.net.client.packets;

import com.astetyne.expirium.main.world.tiles.Tile;
import com.astetyne.expirium.server.backend.Packable;

import java.nio.ByteBuffer;

public class TileBreakReqPacket implements Packable {

    private final int chunkID, x, y;

    public TileBreakReqPacket(Tile t) {
        this.chunkID = t.getChunk().getId();
        this.x = t.getX();
        this.y = t.getY();
    }

    @Override
    public int getPacketID() {
        return 15;
    }

    @Override
    public void populateWithData(ByteBuffer bb) {
        bb.putInt(chunkID);
        bb.putInt(x);
        bb.putInt(y);
    }
}
