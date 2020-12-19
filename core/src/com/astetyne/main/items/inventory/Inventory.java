package com.astetyne.main.items.inventory;

import com.astetyne.main.Resources;
import com.astetyne.main.gui.HotBarSlot;
import com.astetyne.main.gui.SwitchArrow;
import com.astetyne.main.items.Item;
import com.astetyne.main.items.ItemStack;
import com.astetyne.main.items.ItemType;
import com.astetyne.main.stages.GameStage;

import java.util.ArrayList;
import java.util.List;

public class Inventory {

    private HotBarSlot toolSlot, materialSlot, consumableSlot;
    private final SwitchArrow switchArrowUp, switchArrowDown;

    private List<ItemStack> tools;
    private List<ItemStack> materials;
    private List<ItemStack> consumables;

    private int toolsSwitchIndex = 0;
    private int materialsSwitchIndex = 0;
    private int consumablesSwitchIndex = 0;

    public Inventory() {

        toolSlot = new HotBarSlot(Resources.HOT_BAR_SLOT_STYLE_TOOL, onFocusTool);
        materialSlot = new HotBarSlot(Resources.HOT_BAR_SLOT_STYLE_TOOL, onFocusBuild);
        consumableSlot = new HotBarSlot(Resources.HOT_BAR_SLOT_STYLE_TOOL, onFocusUse);

        toolSlot.setFocus(true);

        switchArrowUp = new SwitchArrow(Resources.SWITCH_ARROW_STYLE, onSwitchUp);
        switchArrowDown = new SwitchArrow(Resources.SWITCH_ARROW_STYLE, onSwitchDown);

        tools = new ArrayList<>();
        materials = new ArrayList<>();
        consumables = new ArrayList<>();

    }

    private final Runnable onFocusTool = () -> {
        materialSlot.setFocus(false);
        consumableSlot.setFocus(false);
        GameStage.get().getGameGUI().buildTableTool();
    };

    private final Runnable onFocusBuild = () -> {
        toolSlot.setFocus(false);
        consumableSlot.setFocus(false);
        GameStage.get().getGameGUI().buildTableBuild();
    };

    private final Runnable onFocusUse = () -> {
        toolSlot.setFocus(false);
        materialSlot.setFocus(false);
        GameStage.get().getGameGUI().buildTableUse();
    };

    private final Runnable onSwitchUp = () -> {
        if(toolSlot.isFocused()) {
            if(toolsSwitchIndex >= tools.size()-1) {
                toolsSwitchIndex = 0;
            }else {
                toolsSwitchIndex++;
            }
        }else if(materialSlot.isFocused()) {
            if(materialsSwitchIndex >= materials.size()-1) {
                materialsSwitchIndex = 0;
            }else {
                materialsSwitchIndex++;
            }
        }else if(consumableSlot.isFocused()) {
            if(consumablesSwitchIndex >= consumables.size()-1) {
                consumablesSwitchIndex = 0;
            }else {
                consumablesSwitchIndex++;
            }
        }
        setItemsToSlots();
    };

    private final Runnable onSwitchDown = () -> {
        if(toolSlot.isFocused()) {
            if(toolsSwitchIndex == 0) {
                toolsSwitchIndex = Math.max(tools.size()-1, 0);
            }else {
                toolsSwitchIndex--;
            }
        }else if(materialSlot.isFocused()) {
            if(materialsSwitchIndex == 0) {
                materialsSwitchIndex = Math.max(materials.size()-1, 0);
            }else {
                materialsSwitchIndex--;
            }
        }else if(consumableSlot.isFocused()) {
            if(consumablesSwitchIndex == 0) {
                consumablesSwitchIndex = Math.max(consumables.size()-1, 0);
            }else {
                consumablesSwitchIndex--;
            }
        }
        setItemsToSlots();
    };

    public void onItemPick(Item item) {

        for(ItemStack is : materials) {
            if(is.getItem().getType() == item.getType()) {
                is.increaseAmount();
                return;
            }
        }
        materials.add(new ItemStack(item, 1));
        //todo: spravit aj pre ostatne typy
        setItemsToSlots();
    }

    public void removeItem(ItemType type) {

        if(type.getCategory() == 0) {

        }else if(type.getCategory() == 1) {

            for(ItemStack is : materials) {
                if(is.getItem().getType() == type) {
                    is.decreaseAmount();
                    //todo: kontrola ci je 0 - prazdny
                }
            }

        }else if(type.getCategory() == 2) {

        }
    }

    private void setItemsToSlots() {

        if(tools.size() != 0) {
            toolSlot.setItemStack(tools.get(toolsSwitchIndex));
        }else {
            toolSlot.setItemStack(null);
        }

        if(materials.size() != 0) {
            materialSlot.setItemStack(materials.get(materialsSwitchIndex));
        }else {
            materialSlot.setItemStack(null);
        }

        if(consumables.size() != 0) {
            consumableSlot.setItemStack(consumables.get(consumablesSwitchIndex));
        }else {
            consumableSlot.setItemStack(null);
        }

    }

    public HotBarSlot getToolSlot() {
        return toolSlot;
    }

    public HotBarSlot getMaterialSlot() {
        return materialSlot;
    }

    public HotBarSlot getConsumableSlot() {
        return consumableSlot;
    }

    public SwitchArrow getSwitchArrowUp() {
        return switchArrowUp;
    }

    public SwitchArrow getSwitchArrowDown() {
        return switchArrowDown;
    }
}
