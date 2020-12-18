package com.astetyne.main.net.server.actions;

import com.astetyne.main.items.ItemType;
import com.astetyne.main.net.netobjects.MessageAction;

public class ItemPickupAction extends MessageAction {

    private final ItemType item;

    public ItemPickupAction(ItemType item) {
        this.item = item;
    }

    public ItemType getItem() {
        return item;
    }
}
