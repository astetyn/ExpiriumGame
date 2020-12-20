package com.astetyne.server.backend.packets;

import com.astetyne.main.items.ItemType;

import java.io.Serializable;

public class ItemPickupAction implements Serializable {

    private final ItemType item;

    public ItemPickupAction(ItemType item) {
        this.item = item;
    }

    public ItemType getItem() {
        return item;
    }
}
