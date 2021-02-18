package com.astetyne.expirium.server.core.entity.player;

import com.astetyne.expirium.client.data.ThumbStickData;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.world.ExpiWorld;

public abstract class ToolManager {

    protected final ExpiServer server;
    protected final ExpiPlayer owner;
    protected final ExpiWorld world;

    public ToolManager(ExpiServer server, ExpiPlayer owner) {
        this.server = server;
        this.owner = owner;
        world = server.getWorld();
    }

    public abstract void onTick(ThumbStickData data);

    public void end() {}
}
