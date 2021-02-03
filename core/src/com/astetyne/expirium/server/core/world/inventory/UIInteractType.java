package com.astetyne.expirium.server.core.world.inventory;

import java.util.HashMap;

public enum UIInteractType {

    SWITCH_UP,
    SWITCH_DOWN,
    SLOT_TOOLS,
    SLOT_MATERIALS,
    SLOT_CONSUMABLE,
    OPEN_INV,
    CONSUME_BUTTON;

    int id;

    public static UIInteractType getType(int id) {
        return map.get(id);
    }

    public int getID() {
        return id;
    }

    private static final HashMap<Integer, UIInteractType> map;
    static {
        map = new HashMap<>();
        int i = 0;
        for(UIInteractType tt : UIInteractType.values()) {
            tt.id = i;
            map.put(tt.id, tt);
            i++;
        }
    }

}
