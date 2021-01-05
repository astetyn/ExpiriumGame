package com.astetyne.expirium.server.api.world.inventory;

import java.util.HashMap;

public enum ChosenSlot {

    TOOL_SLOT(InvInteractType.SLOT_TOOLS),
    MATERIAL_SLOT(InvInteractType.SLOT_MATERIALS),
    CONSUMABLE_SLOT(InvInteractType.SLOT_CONSUMABLE);

    int id;
    InvInteractType onClick;

    ChosenSlot(InvInteractType onClick) {
        this.onClick = onClick;
    }

    public static ChosenSlot getSlot(int i) {
        return map.get(i);
    }

    public int getId() {
        return id;
    }

    public InvInteractType getOnClick() {
        return onClick;
    }

    private static final HashMap<Integer, ChosenSlot> map;
    static {
        map = new HashMap<>();
        int i = 0;
        for(ChosenSlot chs : ChosenSlot.values()) {
            chs.id = i;
            map.put(chs.id, chs);
            i++;
        }
    }

}
