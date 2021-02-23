package com.astetyne.expirium.server.core.world;

import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.entity.player.Player;

public class WorldLoader {

    private final Player owner;
    private int layer;
    private final World world;
    boolean completed;

    public WorldLoader(ExpiServer server, Player owner) {
        this.owner = owner;
        layer = 0;
        world = server.getWorld();
        completed = false;
    }

    public void onTick() {
        if(owner.getGateway().getOut().occupied() > 0.5f) return;
        owner.getNetManager().putWorldFeedPacket(layer);
        layer++;
        if(layer == world.getTerrainHeight()) {
            completed = true;
        }
    }

    public boolean isCompleted() {
        return completed;
    }

}
