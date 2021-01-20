package com.astetyne.expirium.server.api.world.listeners;

import com.astetyne.expirium.client.tiles.TileType;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.Saveable;
import com.astetyne.expirium.server.api.event.Source;
import com.astetyne.expirium.server.api.event.TileChangeEvent;
import com.astetyne.expirium.server.api.event.TileChangeListener;
import com.astetyne.expirium.server.api.world.tiles.Campfire;
import com.astetyne.expirium.server.api.world.tiles.ExpiTile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class CampfireListener implements TileChangeListener, Saveable {

    private final HashMap<ExpiTile, Campfire> campfires;

    public CampfireListener() {
        campfires = new HashMap<>();
        GameServer.get().getEventManager().getTileChangeListeners().add(this);
    }

    public CampfireListener(DataInputStream in) throws IOException {
        campfires = new HashMap<>();

        int number = in.readInt();
        for(int i = 0; i < number; i++) {
            Campfire cf = new Campfire(in);
            campfires.put(cf.getTile(), cf);
        }

        GameServer.get().getEventManager().getTileChangeListeners().add(this);
    }

    @Override
    public void onTileChange(TileChangeEvent event) {

        TileType from = event.getFrom();
        TileType toType = event.getTile().getTypeFront();

        if(event.getSource() == Source.NATURAL && from == TileType.CAMPFIRE_BIG && toType == TileType.CAMPFIRE_SMALL) {
            return;
        }

        if(from == TileType.CAMPFIRE_BIG || from == TileType.CAMPFIRE_SMALL) {
            campfires.remove(event.getTile());
        }

        if(toType == TileType.CAMPFIRE_BIG || toType == TileType.CAMPFIRE_SMALL) {
            Campfire cf = new Campfire(event.getTile());
            campfires.put(cf.getTile(), cf);
        }

    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeInt(campfires.size());
        for(Campfire cf : campfires.values()) {
            cf.writeData(out);
        }
    }
}
