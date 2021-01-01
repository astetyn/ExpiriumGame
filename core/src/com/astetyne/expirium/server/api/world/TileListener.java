package com.astetyne.expirium.server.api.world;

public interface TileListener {

    void onTick();

    void onTilePreBreak(ExpiTile t);

    void onTilePlace(ExpiTile t);

    void onTileInteract(ExpiTile t);

}
