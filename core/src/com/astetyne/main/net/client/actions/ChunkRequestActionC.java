package com.astetyne.main.net.client.actions;

public class ChunkRequestActionC extends ClientAction {

    private final int chunkId;

    public ChunkRequestActionC(int chunkId) {
        this.chunkId = chunkId;
    }

    public int getChunkId() {
        return chunkId;
    }
}
