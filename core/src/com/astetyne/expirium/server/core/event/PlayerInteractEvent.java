package com.astetyne.expirium.server.core.event;

import com.astetyne.expirium.client.world.input.InteractType;
import com.astetyne.expirium.server.core.entity.ExpiPlayer;
import com.astetyne.expirium.server.core.world.tile.ExpiTile;
import com.badlogic.gdx.math.Vector2;

public class PlayerInteractEvent implements Cancellable {

    private final ExpiPlayer player;
    private final Vector2 loc;
    private final ExpiTile tile;
    private final InteractType type;
    private boolean cancelled;

    public PlayerInteractEvent(ExpiPlayer player, float x, float y, ExpiTile tile, InteractType type) {
        this.player = player;
        loc = new Vector2(x, y);
        this.tile = tile;
        this.type = type;
        cancelled = false;
    }

    public ExpiPlayer getPlayer() {
        return player;
    }

    public Vector2 getLoc() {
        return loc;
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
