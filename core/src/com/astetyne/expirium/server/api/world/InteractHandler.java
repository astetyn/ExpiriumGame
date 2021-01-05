package com.astetyne.expirium.server.api.world;

import com.astetyne.expirium.main.items.ItemCategory;
import com.astetyne.expirium.main.world.input.InteractType;
import com.astetyne.expirium.main.world.tiles.TileType;
import com.astetyne.expirium.server.api.entities.ExpiPlayer;

public class InteractHandler {

    private final ExpiWorld world;

    public InteractHandler(ExpiWorld world) {
        this.world = world;
    }

    public void onInteract(ExpiPlayer p, float x, float y, InteractType type) {

        ExpiTile t = world.getTileAt(x, y);

        if(type == InteractType.PRESS || type == InteractType.DRAG) {
            if(p.getInv().getItemInHand().getItem().getCategory() == ItemCategory.MATERIAL && t.getType() == TileType.AIR) {
                world.onTilePlaceReq(t, p.getInv().getItemInHand().getItem(), p);
            }
        }

        for(TileListener l : world.getTileListeners()) {
            l.onTileInteract(t);
        }

    }

}
