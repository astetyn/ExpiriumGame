package com.astetyne.expirium.server.core.entity.player;

import com.astetyne.expirium.client.data.ThumbStickData;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.world.World;

public abstract class ToolManager {

    protected final ExpiServer server;
    protected final Player owner;
    protected final World world;

    public ToolManager(ExpiServer server, Player owner) {
        this.server = server;
        this.owner = owner;
        world = server.getWorld();
    }

    public abstract void onTick(ThumbStickData data);

    public void end() {}
}
