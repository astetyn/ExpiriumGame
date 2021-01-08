package com.astetyne.expirium.server.api.world.event;

import com.astetyne.expirium.main.world.tiles.TileType;
import com.astetyne.expirium.server.api.entity.ExpiPlayer;
import com.astetyne.expirium.server.api.world.ExpiTile;

import java.util.HashSet;

public class TileChangeEvent implements Cancellable {

    private static final HashSet<TileChangeListener> listeners = new HashSet<>();

    private final ExpiTile tile;
    private final TileType from;
    private final ExpiPlayer player;
    private final Source source;
    private boolean cancelled;

    public TileChangeEvent(ExpiTile tile, TileType from, ExpiPlayer player, Source source) {
        this.tile = tile;
        this.from = from;
        this.player = player;
        this.source = source;
        cancelled = false;

        for(TileChangeListener listener :  listeners) {
            listener.onTileChange(this);
        }
    }

    public ExpiTile getTile() {
        return tile;
    }

    public TileType getFrom() {
        return from;
    }

    public ExpiPlayer getPlayer() {
        return player;
    }

    public Source getSource() {
        return source;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public static HashSet<TileChangeListener> getListeners() {
        return listeners;
    }

}
