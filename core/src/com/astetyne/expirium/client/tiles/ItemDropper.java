package com.astetyne.expirium.client.tiles;

import com.astetyne.expirium.client.items.Item;

public class ItemDropper {

    public final Item[] items;
    public final float[] chances;

    public ItemDropper(Item[] items, float... chances) {
        this.items = items;
        this.chances = chances;
    }

}
