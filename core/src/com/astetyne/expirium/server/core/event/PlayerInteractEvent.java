package com.astetyne.expirium.server.core.event;

import com.astetyne.expirium.client.world.input.InteractType;
import com.astetyne.expirium.server.core.entity.player.Player;
import com.astetyne.expirium.server.core.world.tile.Tile;
import com.badlogic.gdx.math.Vector2;

public class PlayerInteractEvent implements Cancellable {

    private final Player player;
    private final Vector2 loc;
    private final Tile tile;
    private final InteractType type;
    private boolean cancelled;

    public PlayerInteractEvent(Player player, float x, float y, Tile tile, InteractType type) {
        this.player = player;
        loc = new Vector2(x, y);
        this.tile = tile;
        this.type = type;
        cancelled = false;
    }

    public Player getPlayer() {
        return player;
    }

    public Vector2 getLoc() {
        return loc;
    }

    public Tile getTile() {
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
