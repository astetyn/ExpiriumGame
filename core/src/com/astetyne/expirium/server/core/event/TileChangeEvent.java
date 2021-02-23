package com.astetyne.expirium.server.core.event;

import com.astetyne.expirium.server.core.world.tile.Material;
import com.astetyne.expirium.server.core.world.tile.MetaTile;
import com.astetyne.expirium.server.core.world.tile.Tile;

public class TileChangeEvent implements Cancellable {

    private final Tile tile;
    private final Material fromMat;
    private final MetaTile fromMeta;
    private final Source source;
    private boolean cancelled;

    public TileChangeEvent(Tile tile, MetaTile fromMeta, Material fromMat, Source source) {
        this.tile = tile;
        this.fromMat = fromMat;
        this.fromMeta = fromMeta;
        this.source = source;
        cancelled = false;
    }

    public Tile getTile() {
        return tile;
    }

    public Material getFromMat() {
        return fromMat;
    }

    public MetaTile getFromMeta() {
        return fromMeta;
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

}
