package com.astetyne.server.backend.packets;

import com.astetyne.main.items.ItemType;

import java.io.Serializable;

public class ItemDespawnAction implements Serializable {

    private final int ID;
    private final ItemType item;

    public ItemDespawnAction(int ID, ItemType item) {
        this.ID = ID;
        this.item = item;
    }

    public int getID() {
        return ID;
    }

    public ItemType getItem() {
        return item;
    }
}
