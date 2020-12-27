package com.astetyne.expirium.main.items.inventory;

import com.astetyne.expirium.main.Resources;
import com.astetyne.expirium.main.gui.GameGUILayout;
import com.astetyne.expirium.main.gui.HotBarSlot;
import com.astetyne.expirium.main.gui.InvGUILayout;
import com.astetyne.expirium.main.gui.SwitchArrow;
import com.astetyne.expirium.main.items.Item;
import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.items.ItemType;
import com.astetyne.expirium.main.stages.GameStage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.ArrayList;
import java.util.List;

public class Inventory {

    private final InvGUILayout invGUILayout;

    private HotBarSlot toolSlot, materialSlot, consumableSlot;
    private final SwitchArrow switchArrowUp, switchArrowDown;
    private final ImageButton inventoryButton, consumeButton;

    private List<ItemStack> tools;
    private List<ItemStack> materials;
    private List<ItemStack> consumables;

    private int toolsSwitchIndex = 0;
    private int materialsSwitchIndex = 0;
    private int consumablesSwitchIndex = 0;

    public Inventory() {

        invGUILayout = new InvGUILayout();

        toolSlot = new HotBarSlot(Resources.HOT_BAR_SLOT_STYLE_TOOL, onFocusTool);
        materialSlot = new HotBarSlot(Resources.HOT_BAR_SLOT_STYLE_TOOL, onFocusBuild);
        consumableSlot = new HotBarSlot(Resources.HOT_BAR_SLOT_STYLE_TOOL, onFocusUse);

        toolSlot.setFocus(true);

        switchArrowUp = new SwitchArrow(Resources.SWITCH_ARROW_STYLE, onSwitchUp, false);
        switchArrowDown = new SwitchArrow(Resources.SWITCH_ARROW_STYLE, onSwitchDown, true);
        inventoryButton = new ImageButton(new TextureRegionDrawable(Resources.WOOD_TEXTURE));
        consumeButton = new ImageButton(new TextureRegionDrawable(Resources.WOOD_TEXTURE));

        inventoryButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameStage.get().setActiveGuiLayout(invGUILayout);
            }
        });

        tools = new ArrayList<>();
        materials = new ArrayList<>();
        consumables = new ArrayList<>();

    }

    private final Runnable onFocusTool = () -> {
        materialSlot.setFocus(false);
        consumableSlot.setFocus(false);
        ((GameGUILayout) GameStage.get().getGuiLayout()).buildTableTool();
    };

    private final Runnable onFocusBuild = () -> {
        toolSlot.setFocus(false);
        consumableSlot.setFocus(false);
        ((GameGUILayout) GameStage.get().getGuiLayout()).buildTableBuild();
    };

    private final Runnable onFocusUse = () -> {
        toolSlot.setFocus(false);
        materialSlot.setFocus(false);
        ((GameGUILayout) GameStage.get().getGuiLayout()).buildTableUse();
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

    public ImageButton getInventoryButton() {
        return inventoryButton;
    }

    public ImageButton getConsumeButton() {
        return consumeButton;
    }
}
