package com.astetyne.expirium.server.api.event;

import com.astetyne.expirium.client.world.input.InteractType;
import com.astetyne.expirium.server.api.entity.ExpiPlayer;
import com.astetyne.expirium.server.api.world.tiles.ExpiTile;

public class PlayerInteractEvent implements Cancellable {

    private final ExpiPlayer player;
    private final float x, y;
    private final ExpiTile tile;
    private final InteractType type;
    private boolean cancelled;

    public PlayerInteractEvent(ExpiPlayer player, float x, float y, ExpiTile tile, InteractType type) {
        this.player = player;
        this.x = x;
        this.y = y;
        this.tile = tile;
        this.type = type;
        cancelled = false;
    }

    public ExpiPlayer getPlayer() {
        return player;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public ExpiTile getTile() {
        return tile;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public InteractType getType() {
        return type;
    }
}
