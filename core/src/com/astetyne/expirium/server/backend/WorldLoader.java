package com.astetyne.expirium.server.backend;

import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.entity.ExpiPlayer;
import com.astetyne.expirium.server.api.world.ExpiWorld;

public class WorldLoader {

    private final ExpiPlayer owner;
    private int partNumber;
    private final ExpiWorld world;

    public WorldLoader(ExpiPlayer owner) {
        this.owner = owner;
        partNumber = 0;
        world = GameServer.get().getWorld();
    }

    public void update() {
        if(owner.getGateway().getOut().occupied() > 0.5f) return;
        owner.getNetManager().putWorldFeedPacket(world.getTerrain(), world.getPartHeight(), partNumber);
        partNumber++;
    }

    public boolean isCompleted() {
        return world.getTerrainHeight() / world.getPartHeight() == partNumber;
    }

}
