package com.astetyne.server.backend.packets;

import com.astetyne.server.backend.Packable;

import java.nio.ByteBuffer;
import java.util.List;

public class ChunkDestroyPacket implements Packable {

    private final int chunkID;
    private final List<Integer> fixturesID;

    public ChunkDestroyPacket(int chunkID, List<Integer> fixturesID) {
        this.chunkID = chunkID;
        this.fixturesID = fixturesID;
    }

    @Override
    public int getPacketID() {
        return 12;
    }

    @Override
    public byte[] toByteArray() {

        ByteBuffer bb = ByteBuffer.allocate(4 + 4 + fixturesID.size()*4);

        bb.putInt(getPacketID());
        bb.putInt(chunkID);
        for(int e : fixturesID) {
            bb.putInt(e);
        }

        return bb.array();
    }
}
