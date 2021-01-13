package com.astetyne.expirium.server.api.world;

import com.astetyne.expirium.main.items.ItemCategory;
import com.astetyne.expirium.main.world.input.InteractType;
import com.astetyne.expirium.main.world.tiles.TileType;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.entity.ExpiPlayer;
import com.astetyne.expirium.server.api.event.PlayerInteractEvent;
import com.astetyne.expirium.server.api.event.PlayerInteractListener;

import java.util.List;

public class InteractHandler {

    private final ExpiWorld world;

    public InteractHandler(ExpiWorld world) {
        this.world = world;
    }

    public void onInteract(ExpiPlayer p, float x, float y, InteractType type) {

        ExpiTile t = world.getTileAt(x, y);

        if(type == InteractType.PRESS || type == InteractType.DRAG) {
            if(p.getInv().getItemInHand().getItem().getCategory() == ItemCategory.MATERIAL && t.getTypeFront() == TileType.AIR) {
                world.onTilePlaceReq(t, p.getInv().getItemInHand().getItem(), p);
                return;
            }
        }
        PlayerInteractEvent e = new PlayerInteractEvent(p, x, y, t);
        List<PlayerInteractListener> list = GameServer.get().getEventManager().getPlayerInteractListeners();
        for(int i = list.size() - 1; i >= 0; i--) {
            list.get(i).onInteract(e);
        }
    }

}
