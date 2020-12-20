package com.astetyne.main.net.client.packets;

import com.astetyne.main.items.ItemType;

import java.io.Serializable;

public class TilePlaceActionCS implements Serializable {

    private final int playerID;
    private final int chunkID, x, y;
    private final ItemType placedItem;

    public TilePlaceActionCS(TilePlaceActionCS tpa) {
        this.playerID = tpa.getPlayerID();
        this.chunkID = tpa.getChunkID();
        this.x = tpa.getX();
        this.y = tpa.getY();
        this.placedItem = tpa.getPlacedItem();
    }

    public TilePlaceActionCS(int playerID, int chunkID, int x, int y, ItemType placedItem) {
        this.playerID = playerID;
        this.chunkID = chunkID;
        this.x = x;
        this.y = y;
        this.placedItem = placedItem;
    }

    public int getPlayerID() {
        return playerID;
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

    public ItemType getPlacedItem() {
        return placedItem;
    }
}
