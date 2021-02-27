package com.astetyne.expirium.server.core.entity.player;

import com.astetyne.expirium.client.data.ThumbStickData;
import com.astetyne.expirium.client.world.input.InteractType;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.world.World;
import com.astetyne.expirium.server.core.world.tile.Tile;

public abstract class ToolManager {

    protected final ExpiServer server;
    protected final Player owner;
    protected final World world;
    protected final ThumbStickData data;

    public ToolManager(ExpiServer server, Player owner, ThumbStickData data) {
        this.server = server;
        this.owner = owner;
        world = server.getWorld();
        this.data = data;
    }

    public void onInteract(Tile t, InteractType type) {}

    public abstract void onTick();

    public void end() {}
}
