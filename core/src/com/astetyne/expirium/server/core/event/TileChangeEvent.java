package com.astetyne.expirium.server.core.event;

import com.astetyne.expirium.client.tiles.Material;
import com.astetyne.expirium.server.core.world.tile.ExpiTile;
import com.astetyne.expirium.server.core.world.tile.MetaTile;

public class TileChangeEvent implements Cancellable {

    private final ExpiTile tile;
    private final Material fromMat;
    private final MetaTile fromMeta;
    private final Source source;
    private boolean cancelled;

    public TileChangeEvent(ExpiTile tile, MetaTile fromMeta, Material fromMat, Source source) {
        this.tile = tile;
        this.fromMat = fromMat;
        this.fromMeta = fromMeta;
        this.source = source;
        cancelled = false;
    }

    public ExpiTile getTile() {
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
