package com.astetyne.expirium.server.core.world.modules;

import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.Saveable;
import com.astetyne.expirium.server.core.event.TileChangeEvent;
import com.astetyne.expirium.server.core.event.TileChangeListener;
import com.astetyne.expirium.server.core.world.tile.Campfire;
import com.astetyne.expirium.server.core.world.tile.ExpiTile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class CampfireListener implements TileChangeListener, Saveable {

    private final ExpiServer server;
    private final HashMap<ExpiTile, Campfire> campfires;

    public CampfireListener(ExpiServer server) {
        this.server = server;
        campfires = new HashMap<>();
        server.getEventManager().getTileChangeListeners().add(this);
    }

    public CampfireListener(ExpiServer server, DataInputStream in) throws IOException {
        this.server = server;
        campfires = new HashMap<>();

        int number = in.readInt();
        for(int i = 0; i < number; i++) {
            Campfire cf = new Campfire(server, in);
            campfires.put(cf.getTile(), cf);
        }

        server.getEventManager().getTileChangeListeners().add(this);
    }

    @Override
    public void onTileChange(TileChangeEvent event) {

        /*Material from = event.getFromMat();
        Material toType = event.getTile().getMaterial();

        if(event.getSource() == Source.NATURAL && from == Material.CAMPFIRE_BIG && toType == Material.CAMPFIRE_SMALL) {
            return;
        }

        if(from == Material.CAMPFIRE_BIG || from == Material.CAMPFIRE_SMALL) {
            campfires.remove(event.getTile());
        }

        if(toType == Material.CAMPFIRE_BIG || toType == Material.CAMPFIRE_SMALL) {
            Campfire cf = new Campfire(server, event.getTile());
            campfires.put(cf.getTile(), cf);
        }*/

    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeInt(campfires.size());
        for(Campfire cf : campfires.values()) {
            cf.writeData(out);
        }
    }
}
