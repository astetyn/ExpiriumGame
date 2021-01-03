package com.astetyne.expirium.server.api.world.inventory;

import java.util.HashMap;

public enum InvInteractType {

    SWITCH_UP,
    SWITCH_DOWN,
    SLOT_TOOLS,
    SLOT_MATERIALS, SLOT_CONSUMABLE;

    int id;

    public static InvInteractType getType(int id) {
        return map.get(id);
    }

    public int getID() {
        return id;
    }

    private static final HashMap<Integer, InvInteractType> map;
    static {
        map = new HashMap<>();
        int i = 0;
        for(InvInteractType tt : InvInteractType.values()) {
            tt.id = i;
            map.put(tt.id, tt);
            i++;
        }
    }

}
