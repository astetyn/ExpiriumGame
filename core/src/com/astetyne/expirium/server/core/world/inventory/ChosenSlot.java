package com.astetyne.expirium.server.core.world.inventory;

public enum ChosenSlot {

    TOOL_SLOT(UIInteractType.SLOT_TOOLS),
    MATERIAL_SLOT(UIInteractType.SLOT_MATERIALS),
    CONSUMABLE_SLOT(UIInteractType.SLOT_CONSUMABLE);

    UIInteractType onClick;

    ChosenSlot(UIInteractType onClick) {
        this.onClick = onClick;
    }

    public static ChosenSlot get(int i) {
        return values()[i];
    }

    public UIInteractType getOnClick() {
        return onClick;
    }

}
