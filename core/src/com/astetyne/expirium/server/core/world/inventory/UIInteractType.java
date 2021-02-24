package com.astetyne.expirium.server.core.world.inventory;

public enum UIInteractType {

    SWITCH_UP,
    SWITCH_DOWN,
    SLOT_TOOLS,
    SLOT_MATERIALS,
    SLOT_CONSUMABLE,
    OPEN_INV, //useless?
    CONSUME_BUTTON;

    public static UIInteractType get(int id) {
        return values()[id];
    }

}
