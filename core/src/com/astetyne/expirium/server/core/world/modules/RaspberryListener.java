package com.astetyne.expirium.server.core.world.modules;

import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.tiles.TileType;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.Saveable;
import com.astetyne.expirium.server.core.entity.ExpiPlayer;
import com.astetyne.expirium.server.core.event.*;
import com.astetyne.expirium.server.core.world.tile.ExpiTile;
import com.astetyne.expirium.server.core.world.tile.RaspberryBush;
import com.badlogic.gdx.math.Vector2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class RaspberryListener implements PlayerInteractListener, TileChangeListener, TickListener, Saveable {

    private final ExpiServer server;
    private final List<RaspberryBush> growingBushes;
    private final HashMap<ExpiTile, RaspberryBush> lookUp;

    public RaspberryListener(ExpiServer server) {
        this.server = server;
        growingBushes = new ArrayList<>();
        lookUp = new HashMap<>();
        server.getEventManager().getPlayerInteractListeners().add(this);
        server.getEventManager().getTileChangeListeners().add(this);
        server.getEventManager().getTickListeners().add(this);

        ExpiTile[][] terrain = server.getWorld().getTerrain();
        int w = server.getWorld().getTerrainWidth();
        int h = server.getWorld().getTerrainHeight();

        for(int i = 0; i < h; i++) {
            for(int j = 0; j < w; j++) {
                ExpiTile t = terrain[i][j];
                if(t.getType() == TileType.RASPBERRY_BUSH_1) {
                    RaspberryBush bush = new RaspberryBush(t, (float) (Math.random() * 600));
                    growingBushes.add(bush);
                    lookUp.put(t, bush);
                }
            }
        }

    }

    public RaspberryListener(ExpiServer server, DataInputStream in) throws IOException {
        this.server = server;
        growingBushes = new ArrayList<>();
        lookUp = new HashMap<>();
        server.getEventManager().getPlayerInteractListeners().add(this);
        server.getEventManager().getTileChangeListeners().add(this);
        server.getEventManager().getTickListeners().add(this);

        int bushesNumber = in.readInt();
        for(int i = 0; i < bushesNumber; i++) {
            int x = in.readInt();
            int y = in.readInt();
            float growTime = in.readFloat();
            ExpiTile t = server.getWorld().getTerrain()[y][x];
            RaspberryBush bush = new RaspberryBush(t, growTime);
            growingBushes.add(bush);
            lookUp.put(t, bush);
        }
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {

        if(event.getTile().getType() != TileType.RASPBERRY_BUSH_2) return;

        ExpiPlayer p = event.getPlayer();
        if(!p.isInInteractRadius(event.getLoc())) return;

        // confirmed
        server.getWorld().changeTile(event.getTile(), TileType.RASPBERRY_BUSH_1, false, p, Source.PLAYER);

        for(ExpiPlayer ep : server.getPlayers()) {
            ep.getNetManager().putHandPunchPacket(p);
        }

        int raspNumber = (int)(Math.random() * 2) + 1; // 1-2

        float off = (1 - EntityType.DROPPED_ITEM.getWidth())/2;
        Vector2 dropLoc = new Vector2(event.getTile().getX() + off, event.getTile().getY() + off);
        for(int i = 0; i < raspNumber; i++) {
            server.getWorld().spawnDroppedItem(Item.RASPBERRY, dropLoc, Consts.ITEM_COOLDOWN_BREAK);
        }
    }

    @Override
    public void onTileChange(TileChangeEvent event) {

        if(event.getTile().getType() == TileType.RASPBERRY_BUSH_1) {
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
    public void onTick(float delta) {
        Iterator<RaspberryBush> it = growingBushes.iterator();
        while(it.hasNext()) {
            RaspberryBush bush = it.next();
            bush.decreaseGrowTime(delta);
            if(bush.getGrowTime() <= 0) {
                it.remove();
                lookUp.remove(bush.getTile());
                server.getWorld().changeTile(bush.getTile(), TileType.RASPBERRY_BUSH_2, false, null, Source.NATURAL);
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
