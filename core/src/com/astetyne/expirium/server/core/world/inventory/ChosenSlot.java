package com.astetyne.expirium.server.core.world.inventory;

import java.util.HashMap;

public enum ChosenSlot {

    TOOL_SLOT(UIInteractType.SLOT_TOOLS),
    MATERIAL_SLOT(UIInteractType.SLOT_MATERIALS),
    CONSUMABLE_SLOT(UIInteractType.SLOT_CONSUMABLE);

    int id;
    UIInteractType onClick;

    ChosenSlot(UIInteractType onClick) {
        this.onClick = onClick;
    }

    public static ChosenSlot getSlot(int i) {
        return map.get(i);
    }

    public int getId() {
        return id;
    }

    public UIInteractType getOnClick() {
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
