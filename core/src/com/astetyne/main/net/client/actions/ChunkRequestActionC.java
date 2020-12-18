package com.astetyne.main.net.client.actions;

import com.astetyne.main.net.netobjects.MessageAction;

public class ChunkRequestActionC extends MessageAction {

    private final int chunkId;

    public ChunkRequestActionC(int chunkId) {
        this.chunkId = chunkId;
    }

    public int getChunkId() {
        return chunkId;
    }
}
