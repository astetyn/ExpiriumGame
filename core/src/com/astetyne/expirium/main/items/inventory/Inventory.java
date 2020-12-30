package com.astetyne.expirium.main.items.inventory;

import com.astetyne.expirium.main.ExpiriumGame;
import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.gui.*;
import com.astetyne.expirium.main.items.ItemRecipe;
import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.items.ItemType;
import com.astetyne.expirium.main.stages.GameStage;
import com.astetyne.expirium.main.utils.Constants;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Inventory {

    private final InvGUILayout invGUILayout;

    private HotBarSlot toolSlot, materialSlot, consumableSlot;
    private final SwitchArrow switchArrowUp, switchArrowDown;
    private final ImageButton inventoryButton, consumeButton;
    private StorageGrid inventoryGrid;

    private final List<ItemRecipe> itemRecipes;

    private int toolsSwitchIndex = 0;
    private int materialsSwitchIndex = 0;
    private int consumablesSwitchIndex = 0;

    private final HashMap<Integer, StorageGrid> storageGridIDs;

    public Inventory() {

        itemRecipes = new ArrayList<>();
        fillRecipes();

        storageGridIDs = new HashMap<>();

        int c = Constants.PLAYER_INV_COLUMNS;
        int r = Constants.PLAYER_INV_ROWS;
        inventoryGrid = new StorageGrid(c, r, new StorageGrid.StorageGridStyle(Res.INV_TILE_TEX), reloadSlotItems);

        invGUILayout = new InvGUILayout();

        toolSlot = new HotBarSlot(Res.HOT_BAR_SLOT_STYLE_TOOL, onFocusTool);
        materialSlot = new HotBarSlot(Res.HOT_BAR_SLOT_STYLE_TOOL, onFocusBuild);
        consumableSlot = new HotBarSlot(Res.HOT_BAR_SLOT_STYLE_TOOL, onFocusUse);

        toolSlot.setFocus(true);

        switchArrowUp = new SwitchArrow(Res.SWITCH_ARROW_STYLE, onSwitchUp, false);
        switchArrowDown = new SwitchArrow(Res.SWITCH_ARROW_STYLE, onSwitchDown, true);
        inventoryButton = new ImageButton(new TextureRegionDrawable(Res.INVENTORY_TEXTURE));
        consumeButton = new ImageButton(new TextureRegionDrawable(Res.WOOD_TEXTURE));

        inventoryButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(inventoryGrid.getId() == -1) {
                    return;
                }
                GameStage.get().setActiveGuiLayout(invGUILayout);
                ExpiriumGame.get().getClientGateway().getManager().putInvOpenReqPacket(inventoryGrid.getId());
            }
        });

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

    private final Runnable reloadSlotItems = () -> {

        int toolsCount = 0;
        int materialsCount = 0;
        int consumableCount = 0;

        ItemStack lastTool = null, lastMaterial = null, lastConsumable = null;

        for(ItemStack is : inventoryGrid.getItems()) {
            if(is.getItem().getCategory() == 0) {
                if(toolsSwitchIndex == toolsCount) {
                    toolSlot.setItemStack(is);
                }
                lastTool = is;
                toolsCount++;
            }else if(is.getItem().getCategory() == 1) {
                if(materialsSwitchIndex == materialsCount) {
                    materialSlot.setItemStack(is);
                }
                lastMaterial = is;
                materialsCount++;
            }else if(is.getItem().getCategory() == 2) {
                if(consumablesSwitchIndex == consumableCount) {
                    consumableSlot.setItemStack(is);
                }
                lastConsumable = is;
                consumableCount++;
            }
        }

        if(toolsSwitchIndex >= toolsCount) {
            toolsSwitchIndex = Math.max(toolsCount-1, 0);
            toolSlot.setItemStack(lastTool);
        }
        if(materialsSwitchIndex >= materialsCount) {
            materialsSwitchIndex = Math.max(materialsCount-1, 0);
            materialSlot.setItemStack(lastMaterial);
        }
        if(consumablesSwitchIndex >= consumableCount) {
            consumablesSwitchIndex = Math.max(consumableCount-1, 0);
            consumableSlot.setItemStack(lastConsumable);
        }

    };

    private final Runnable onSwitchDown = () -> {

        if(toolSlot.isFocused()) {
            if(toolsSwitchIndex > 0) toolsSwitchIndex--;
        }else if(materialSlot.isFocused()) {
            if(materialsSwitchIndex > 0) materialsSwitchIndex--;
        }else if(consumableSlot.isFocused()) {
            if(consumablesSwitchIndex > 0) consumablesSwitchIndex--;
        }
        reloadSlotItems.run();
    };

    private final Runnable onSwitchUp = () -> {
        if(toolSlot.isFocused()) {
            toolsSwitchIndex++;
        }else if(materialSlot.isFocused()) {
            materialsSwitchIndex++;
        }else if(consumableSlot.isFocused()) {
            consumablesSwitchIndex++;
        }
        reloadSlotItems.run();
    };

    private void fillRecipes() {
        ItemRecipe torch = new ItemRecipe(new ItemStack(ItemType.RAW_WOOD));
        itemRecipes.add(torch);
        itemRecipes.add(torch);
        itemRecipes.add(torch);
        itemRecipes.add(torch);
        itemRecipes.add(torch);
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

    public StorageGrid getInventoryGrid() {
        return inventoryGrid;
    }

    public HashMap<Integer, StorageGrid> getStorageGridIDs() {
        return storageGridIDs;
    }

    public List<ItemRecipe> getItemRecipes() {
        return itemRecipes;
    }
}
