package com.astetyne.main.net.server.actions;

import com.astetyne.main.net.netobjects.MessageAction;
import com.astetyne.main.net.netobjects.SWorldChunk;

public class ChunkFeedActionS extends MessageAction {

    private final SWorldChunk chunk;

    public ChunkFeedActionS(SWorldChunk chunk) {
        this.chunk = chunk;
    }

    public SWorldChunk getChunk() {
        return chunk;
    }
}
