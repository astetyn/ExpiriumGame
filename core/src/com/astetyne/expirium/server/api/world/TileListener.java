package com.astetyne.expirium.server.api.world;

public interface TileListener {

    void onTick();

    void onTileChange(ExpiTile t);

    void onTilePlace(ExpiTile t);

    void onTileInteract(ExpiTile t);

}
