package com.astetyne.expirium.server.api.world.listeners;

import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.main.world.tiles.TileType;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.entities.ExpiPlayer;
import com.astetyne.expirium.server.api.world.ExpiTile;
import com.astetyne.expirium.server.api.world.event.*;
import com.astetyne.expirium.server.api.world.inventory.CookingInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CampfireListener implements TileChangeListener {

    private final List<Campfire> activeCampfires;
    private final HashMap<ExpiTile, Campfire> lookUp;

    public CampfireListener() {
        activeCampfires = new ArrayList<>();
        lookUp = new HashMap<>();
        TileChangeEvent.getListeners().add(this);
    }

    @Override
    public void onTileChange(TileChangeEvent event) {

        TileType from = event.getFrom();
        TileType toType = event.getTile().getType();

        if(from == TileType.CAMPFIRE_BIG && toType == TileType.CAMPFIRE_SMALL) {
            return;
        }

        if(from == TileType.CAMPFIRE_BIG || from == TileType.CAMPFIRE_SMALL) {
            Campfire campfire = lookUp.get(event.getTile());
            activeCampfires.remove(campfire);
            PlayerInteractEvent.getListeners().remove(campfire);
            ServerTickEvent.getListeners().remove(campfire);
        }

        if(toType == TileType.CAMPFIRE_BIG) {
            Campfire cf = new Campfire(event.getTile());
            PlayerInteractEvent.getListeners().add(cf);
            ServerTickEvent.getListeners().add(cf);
            activeCampfires.add(cf);
            lookUp.put(cf.tile, cf);
        }

    }

    class Campfire implements PlayerInteractListener, TickListener {

        private final ExpiTile tile;
        private float remainingTime;
        private final CookingInventory inventory;
        private final long placeTime;

        public Campfire(ExpiTile tile) {
            this.tile = tile;
            this.remainingTime = Consts.CAMPFIRE_TIME;
            this.inventory = new CookingInventory(2, 2, 5);
            placeTime = System.currentTimeMillis();
        }

        @Override
        public void onInteract(PlayerInteractEvent event) {
            if(event.getTile() != tile || placeTime + 500 > System.currentTimeMillis()) return;
            ExpiPlayer p = event.getPlayer();
            p.setSecondInv(inventory);
            p.getGateway().getManager().putOpenDoubleInvPacket(inventory);
            p.getGateway().getManager().putInvFeedPacket(p);
        }

        @Override
        public void onTick() {
            remainingTime -= 1f / Consts.SERVER_DEFAULT_TPS;
            if(remainingTime <= 0) {
                GameServer.get().getWorld().changeTile(tile, TileType.AIR, false, null, Source.SERVER);
                activeCampfires.remove(this);
            }else if(tile.getType() == TileType.CAMPFIRE_BIG && remainingTime < 5) {
                GameServer.get().getWorld().changeTile(tile, TileType.CAMPFIRE_SMALL, false, null, Source.SERVER);
            }
            inventory.onTick();
        }
    }
}
