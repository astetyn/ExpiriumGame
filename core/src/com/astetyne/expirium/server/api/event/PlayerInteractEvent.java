package com.astetyne.expirium.server.api.event;

import com.astetyne.expirium.server.api.entity.ExpiPlayer;
import com.astetyne.expirium.server.api.world.ExpiTile;

public class PlayerInteractEvent implements Cancellable {

    private final ExpiPlayer player;
    private final float x, y;
    private final ExpiTile tile;
    private boolean cancelled;

    public PlayerInteractEvent(ExpiPlayer player, float x, float y, ExpiTile tile) {
        this.player = player;
        this.x = x;
        this.y = y;
        this.tile = tile;
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

}
