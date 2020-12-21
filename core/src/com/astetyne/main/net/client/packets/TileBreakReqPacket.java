package com.astetyne.main.net.client.packets;

import com.astetyne.main.world.tiles.Tile;
import com.astetyne.server.backend.Packable;

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
    public byte[] toByteArray() {

        ByteBuffer bb = ByteBuffer.allocate(4 + 3*4);
        bb.putInt(getPacketID());

        bb.putInt(chunkID);
        bb.putInt(x);
        bb.putInt(y);

        return bb.array();
    }
}
