package com.astetyne.main.net.netobjects;

import com.astetyne.main.items.ItemType;
import com.astetyne.main.net.server.entities.ServerDroppedItem;

public class SDroppedItem extends SEntity {

    private final ItemType type;

    public SDroppedItem(ServerDroppedItem entity) {
        super(entity);
        this.type = entity.getType();
    }

    public ItemType getType() {
        return type;
    }
}
