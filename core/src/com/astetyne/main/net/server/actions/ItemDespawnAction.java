package com.astetyne.main.net.server.actions;

import com.astetyne.main.items.ItemType;

public class ItemDespawnAction extends ServerAction {

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
