package com.astetyne.main.net.netobjects;

import com.astetyne.main.items.ItemType;
import com.astetyne.server.api.entities.ExpiDroppedItem;

public class SDroppedItem extends SEntity {

    private final ItemType type;

    public SDroppedItem(ExpiDroppedItem entity) {
        super(entity);
        this.type = entity.getItemType();
    }

    public ItemType getType() {
        return type;
    }
}
