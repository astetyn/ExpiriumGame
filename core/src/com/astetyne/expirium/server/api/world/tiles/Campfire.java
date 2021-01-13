package com.astetyne.expirium.server.api.world.tiles;

import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.main.world.tiles.TileType;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.entity.ExpiPlayer;
import com.astetyne.expirium.server.api.event.PlayerInteractEvent;
import com.astetyne.expirium.server.api.event.PlayerInteractListener;
import com.astetyne.expirium.server.api.event.Source;
import com.astetyne.expirium.server.api.event.TickListener;
import com.astetyne.expirium.server.api.world.ExpiTile;
import com.astetyne.expirium.server.api.world.inventory.CookingInventory;

public class Campfire implements PlayerInteractListener, TickListener {

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
        p.getNetManager().putOpenDoubleInvPacket();
        p.getNetManager().putInvFeedPacket();
    }

    @Override
    public void onTick() {
        remainingTime -= 1f / Consts.SERVER_DEFAULT_TPS;
        if(remainingTime <= 0) {
            GameServer.get().getWorld().changeTile(tile, TileType.AIR, false, null, Source.SERVER);
            //activeCampfires.remove(this);
        }else if(tile.getTypeFront() == TileType.CAMPFIRE_BIG && remainingTime < 5) {
            GameServer.get().getWorld().changeTile(tile, TileType.CAMPFIRE_SMALL, false, null, Source.SERVER);
        }
        inventory.onTick();
    }

    public ExpiTile getTile() {
        return tile;
    }
}
