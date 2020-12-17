package com.astetyne.main.net.server.actions;

import com.astetyne.main.items.ItemType;

public class ItemPickupAction extends ServerAction {

    private final ItemType item;

    public ItemPickupAction(ItemType item) {
        this.item = item;
    }

    public ItemType getItem() {
        return item;
    }
}
