package com.astetyne.expirium.server.api.world.listeners;

import com.astetyne.expirium.main.items.Item;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.main.world.tiles.TileType;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.entity.ExpiDroppedItem;
import com.astetyne.expirium.server.api.entity.ExpiPlayer;
import com.astetyne.expirium.server.api.world.ExpiTile;
import com.astetyne.expirium.server.api.world.event.Source;
import com.astetyne.expirium.server.api.world.event.TileChangeEvent;
import com.astetyne.expirium.server.api.world.event.TileChangeListener;
import com.badlogic.gdx.math.Vector2;

public class TreeListener implements TileChangeListener {

    public TreeListener() {
        TileChangeEvent.getListeners().add(this);
    }

    @Override
    public void onTileChange(TileChangeEvent event) {
        ExpiTile t = event.getTile();
        float off = (1 - Consts.D_I_SIZE)/2;

        TileType f = event.getFrom();

        if(event.getSource() == Source.PLAYER && f == TileType.TREE4 || f == TileType.TREE5 || f == TileType.TREE6 ) {
            if(Math.random() > 0.6f) {
                Vector2 vec = new Vector2(t.getX() + off, t.getY() + off);
                ExpiDroppedItem droppedItem = new ExpiDroppedItem(vec, Item.APPLE, Consts.ITEM_COOLDOWN_BREAK);
                for(ExpiPlayer pp : GameServer.get().getPlayers()) {
                    pp.getNetManager().putEntitySpawnPacket(droppedItem);
                }
            }
        }
    }

}
