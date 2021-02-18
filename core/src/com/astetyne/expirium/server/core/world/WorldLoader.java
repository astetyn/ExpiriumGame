package com.astetyne.expirium.server.core.world;

import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.entity.player.ExpiPlayer;

public class WorldLoader {

    private final ExpiPlayer owner;
    private int partNumber;
    private final ExpiWorld world;
    boolean completed;

    public WorldLoader(ExpiServer server, ExpiPlayer owner) {
        this.owner = owner;
        partNumber = 0;
        world = server.getWorld();
        completed = false;
    }

    public void onTick() {
        if(owner.getGateway().getOut().occupied() > 0.5f) return;
        owner.getNetManager().putWorldFeedPacket(world.getTerrain(), world.getPartHeight(), partNumber);
        partNumber++;
        if(world.getTerrainHeight() / world.getPartHeight() == partNumber) {
            completed = true;
        }
    }

    public boolean isCompleted() {
        return completed;
    }

}
