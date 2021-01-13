package com.astetyne.expirium.server.api.world.listeners;

import com.astetyne.expirium.main.world.tiles.TileType;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.event.TileChangeEvent;
import com.astetyne.expirium.server.api.event.TileChangeListener;
import com.astetyne.expirium.server.api.world.ExpiTile;
import com.astetyne.expirium.server.api.world.tiles.Campfire;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CampfireListener implements TileChangeListener {

    private final List<Campfire> activeCampfires;
    private final HashMap<ExpiTile, Campfire> lookUp;

    public CampfireListener() {
        activeCampfires = new ArrayList<>();
        lookUp = new HashMap<>();
        GameServer.get().getEventManager().getTileChangeListeners().add(this);
    }

    @Override
    public void onTileChange(TileChangeEvent event) {

        TileType from = event.getFrom();
        TileType toType = event.getTile().getTypeFront();

        if(from == TileType.CAMPFIRE_BIG && toType == TileType.CAMPFIRE_SMALL) {
            return;
        }

        if(from == TileType.CAMPFIRE_BIG || from == TileType.CAMPFIRE_SMALL) {
            Campfire campfire = lookUp.get(event.getTile());
            activeCampfires.remove(campfire);
            GameServer.get().getEventManager().getPlayerInteractListeners().remove(campfire);
            GameServer.get().getEventManager().getTickListeners().remove(campfire);
        }

        if(toType == TileType.CAMPFIRE_BIG) {
            Campfire cf = new Campfire(event.getTile());
            GameServer.get().getEventManager().getPlayerInteractListeners().add(cf);
            GameServer.get().getEventManager().getTickListeners().add(cf);
            activeCampfires.add(cf);
            lookUp.put(cf.getTile(), cf);
        }

    }
}
