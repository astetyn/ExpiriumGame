package com.astetyne.expirium.server.api.world.listeners;

import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.tiles.TileType;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.api.Saveable;
import com.astetyne.expirium.server.api.entity.ExpiDroppedItem;
import com.astetyne.expirium.server.api.entity.ExpiPlayer;
import com.astetyne.expirium.server.api.event.*;
import com.astetyne.expirium.server.api.world.tiles.ExpiTile;
import com.astetyne.expirium.server.api.world.tiles.RaspberryBush;
import com.badlogic.gdx.math.Vector2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class RaspberryListener implements PlayerInteractListener, TileChangeListener, TickListener, Saveable {

    private final List<RaspberryBush> growingBushes;
    private final HashMap<ExpiTile, RaspberryBush> lookUp;

    public RaspberryListener(ExpiServer expiServer) {
        growingBushes = new ArrayList<>();
        lookUp = new HashMap<>();
        expiServer.getEventManager().getPlayerInteractListeners().add(this);
        expiServer.getEventManager().getTileChangeListeners().add(this);
        expiServer.getEventManager().getTickListeners().add(this);

        ExpiTile[][] terrain = ExpiServer.get().getWorld().getTerrain();
        int w = expiServer.getWorld().getTerrainWidth();
        int h = expiServer.getWorld().getTerrainHeight();

        for(int i = 0; i < h; i++) {
            for(int j = 0; j < w; j++) {
                ExpiTile t = terrain[i][j];
                if(t.getTypeFront() == TileType.RASPBERRY_BUSH_1) {
                    RaspberryBush bush = new RaspberryBush(t, (float) (Math.random() * 600));
                    growingBushes.add(bush);
                    lookUp.put(t, bush);
                }
            }
        }

    }

    public RaspberryListener(DataInputStream in, ExpiServer expiServer) throws IOException {

        growingBushes = new ArrayList<>();
        lookUp = new HashMap<>();
        expiServer.getEventManager().getPlayerInteractListeners().add(this);
        expiServer.getEventManager().getTileChangeListeners().add(this);
        expiServer.getEventManager().getTickListeners().add(this);

        int bushesNumber = in.readInt();
        for(int i = 0; i < bushesNumber; i++) {
            int x = in.readInt();
            int y = in.readInt();
            float growTime = in.readFloat();
            ExpiTile t = expiServer.getWorld().getTerrain()[y][x];
            RaspberryBush bush = new RaspberryBush(t, growTime);
            growingBushes.add(bush);
            lookUp.put(t, bush);
        }
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {

        if(event.getTile().getTypeFront() != TileType.RASPBERRY_BUSH_2) return;

        ExpiServer.get().getWorld().changeTile(event.getTile(), TileType.RASPBERRY_BUSH_1, false, event.getPlayer(), Source.PLAYER);

        int raspNumber = (int)(Math.random() * 2) + 1; // 1-2

        float off = (1 - Consts.D_I_SIZE)/2;
        Vector2 dropLoc = new Vector2(event.getTile().getX() + off, event.getTile().getY() + off);

        for(int i = 0; i < raspNumber; i++) {
            ExpiDroppedItem edi = new ExpiDroppedItem(dropLoc, Item.RASPBERRY, 0.2f);
            for(ExpiPlayer pp : ExpiServer.get().getPlayers()) {
                pp.getNetManager().putEntitySpawnPacket(edi);
            }
        }
    }

    @Override
    public void onTileChange(TileChangeEvent event) {

        if(event.getTile().getTypeFront() == TileType.RASPBERRY_BUSH_1) {
            RaspberryBush bush = new RaspberryBush(event.getTile(), (float) (Math.random() * 600));
            growingBushes.add(bush);
            lookUp.put(event.getTile(), bush);
        }

        if(event.getFrom() == TileType.RASPBERRY_BUSH_1) {
            RaspberryBush bush = lookUp.get(event.getTile());
            if(bush != null) {
                growingBushes.remove(bush);
                lookUp.remove(bush.getTile());
            }
        }

    }

    @Override
    public void onTick() {
        Iterator<RaspberryBush> it = growingBushes.iterator();
        while(it.hasNext()) {
            RaspberryBush bush = it.next();
            bush.decreaseGrowTime(1f / Consts.SERVER_DEFAULT_TPS);
            if(bush.getGrowTime() <= 0) {
                it.remove();
                lookUp.remove(bush.getTile());
                ExpiServer.get().getWorld().changeTile(bush.getTile(), TileType.RASPBERRY_BUSH_2, false, null, Source.NATURAL);
            }
        }
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeInt(growingBushes.size());
        for(RaspberryBush bush : growingBushes) {
            out.writeInt(bush.getTile().getX());
            out.writeInt(bush.getTile().getY());
            out.writeFloat(bush.getGrowTime());
        }
    }
}
