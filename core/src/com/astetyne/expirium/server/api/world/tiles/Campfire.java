package com.astetyne.expirium.server.api.world.tiles;

import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.client.tiles.TileType;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.api.Saveable;
import com.astetyne.expirium.server.api.entity.ExpiDroppedItem;
import com.astetyne.expirium.server.api.entity.ExpiPlayer;
import com.astetyne.expirium.server.api.event.PlayerInteractEvent;
import com.astetyne.expirium.server.api.event.PlayerInteractListener;
import com.astetyne.expirium.server.api.event.Source;
import com.astetyne.expirium.server.api.event.TickListener;
import com.astetyne.expirium.server.api.world.inventory.CookingInventory;
import com.astetyne.expirium.server.net.SimpleServerPacket;
import com.badlogic.gdx.math.Vector2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Campfire implements PlayerInteractListener, TickListener, Saveable {

    private final ExpiTile tile;
    private float remainingTime;
    private final CookingInventory inventory;
    private final long placeTime;

    public Campfire(ExpiTile tile) {
        this.tile = tile;
        this.remainingTime = Consts.CAMPFIRE_TIME;
        this.inventory = new CookingInventory(2, 2, 5);
        placeTime = System.currentTimeMillis();
        ExpiServer.get().getEventManager().getPlayerInteractListeners().add(this);
        ExpiServer.get().getEventManager().getTickListeners().add(this);
    }

    public Campfire(DataInputStream in) throws IOException {
        int x = in.readInt();
        int y = in.readInt();
        remainingTime = in.readFloat();
        tile = ExpiServer.get().getWorld().getTerrain()[y][x];
        inventory = new CookingInventory(2, 2, 5, in);
        placeTime = 0;
        ExpiServer.get().getEventManager().getPlayerInteractListeners().add(this);
        ExpiServer.get().getEventManager().getTickListeners().add(this);
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        if(event.getTile() != tile || placeTime + 500 > System.currentTimeMillis()) return;
        ExpiPlayer p = event.getPlayer();
        if(p.getCenter().dst(event.getX(), event.getY()) > 7) return;
        p.setSecondInv(inventory);
        p.getNetManager().putOpenDoubleInvPacket();
        p.getNetManager().putInvFeedPacket();
    }

    @Override
    public void onTick() {
        remainingTime -= 1f / Consts.SERVER_DEFAULT_TPS;
        if(remainingTime <= 0) {
            ExpiServer.get().getWorld().changeTile(tile, TileType.AIR, false, null, Source.NATURAL);
            ExpiServer.get().getEventManager().getPlayerInteractListeners().remove(this);
            ExpiServer.get().getEventManager().getTickListeners().remove(this);
            for(ExpiPlayer pp : ExpiServer.get().getPlayers()) {
                if(pp.getSecondInv() != inventory) continue;
                pp.getNetManager().putSimpleServerPacket(SimpleServerPacket.CLOSE_DOUBLE_INV);
                float off = (1 - Consts.D_I_SIZE)/2;
                Vector2 dropLoc = new Vector2(tile.getX() + off, tile.getY() + off);
                for(ItemStack is : inventory.getItems()) {
                    for(int i = 0; i < is.getAmount(); i++) {
                        ExpiDroppedItem edi = new ExpiDroppedItem(dropLoc, is.getItem(), 0.5f);
                        for(ExpiPlayer pp2 : ExpiServer.get().getPlayers()) {
                            pp2.getNetManager().putEntitySpawnPacket(edi);
                        }
                    }
                }
                break;
            }
        }else if(tile.getTypeFront() == TileType.CAMPFIRE_BIG && remainingTime < 60) {
            ExpiServer.get().getWorld().changeTile(tile, TileType.CAMPFIRE_SMALL, false, null, Source.NATURAL);
        }
        inventory.onTick();
    }

    public ExpiTile getTile() {
        return tile;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeInt(tile.getX());
        out.writeInt(tile.getY());
        out.writeFloat(remainingTime);
        inventory.writeData(out);
    }
}
