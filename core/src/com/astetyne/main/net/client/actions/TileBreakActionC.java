package com.astetyne.main.net.client.actions;

import com.astetyne.main.items.ItemType;
import com.astetyne.main.world.tiles.Tile;

public class TileBreakActionC extends ClientAction {

    private final int chunkID, x, y;
    private final ItemType dropItem;

    public TileBreakActionC(Tile t, ItemType dropItem) {
        this.chunkID = t.getChunk().getId();
        this.x = t.getX();
        this.y = t.getY();
        this.dropItem = dropItem;
    }

    public int getChunkID() {
        return chunkID;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public ItemType getDropItem() {
        return dropItem;
    }
}
