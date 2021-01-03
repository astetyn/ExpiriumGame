package com.astetyne.expirium.main.items.inventory;

import com.astetyne.expirium.main.ExpiriumGame;
import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.gui.*;
import com.astetyne.expirium.main.items.Item;
import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.stages.GameStage;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.server.api.world.inventory.InvInteractType;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.HashMap;

public class Inventory {

    private final InvGUILayout invGUILayout;

    private HotBarSlot toolSlot, materialSlot, consumableSlot, focusedSlot;
    private final SwitchArrow switchArrowUp, switchArrowDown;
    private final Image inventoryButton, consumeButton;
    private final StorageGrid inventoryGrid;

    private final HashMap<Integer, StorageGrid> storageGridIDs;

    public Inventory() {

        storageGridIDs = new HashMap<>();

        int c = Consts.PLAYER_INV_COLUMNS;
        int r = Consts.PLAYER_INV_ROWS;
        inventoryGrid = new StorageGrid(c, r, new StorageGrid.StorageGridStyle(Res.INV_TILE));

        invGUILayout = new InvGUILayout();

        toolSlot = new HotBarSlot(Res.HOT_BAR_SLOT_STYLE, onFocusTool, "Tools");
        materialSlot = new HotBarSlot(Res.HOT_BAR_SLOT_STYLE, onFocusBuild, "Mats");
        consumableSlot = new HotBarSlot(Res.HOT_BAR_SLOT_STYLE, onFocusUse, "Misc");

        focusedSlot = toolSlot;
        focusedSlot.setFocus(true);

        switchArrowUp = new SwitchArrow(Res.SWITCH_ARROW_STYLE, onSwitchUp, false);
        switchArrowDown = new SwitchArrow(Res.SWITCH_ARROW_STYLE, onSwitchDown, true);
        inventoryButton = new Image(Res.INVENTORY);
        consumeButton = new Image(Res.CAMPFIRE_ITEM);

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
        focusedSlot = toolSlot;
        ExpiriumGame.get().getClientGateway().getManager().putInvInteractPacket(InvInteractType.SLOT_TOOLS);
    };

    private final Runnable onFocusBuild = () -> {
        toolSlot.setFocus(false);
        consumableSlot.setFocus(false);
        ((GameGUILayout) GameStage.get().getGuiLayout()).buildTableBuild();
        focusedSlot = materialSlot;
        ExpiriumGame.get().getClientGateway().getManager().putInvInteractPacket(InvInteractType.SLOT_MATERIALS);
    };

    private final Runnable onFocusUse = () -> {
        toolSlot.setFocus(false);
        materialSlot.setFocus(false);
        ((GameGUILayout) GameStage.get().getGuiLayout()).buildTableUse();
        focusedSlot = consumableSlot;
        ExpiriumGame.get().getClientGateway().getManager().putInvInteractPacket(InvInteractType.SLOT_CONSUMABLE);
    };

    private final Runnable onSwitchUp = () ->
        ExpiriumGame.get().getClientGateway().getManager().putInvInteractPacket(InvInteractType.SWITCH_UP);

    private final Runnable onSwitchDown = () ->
        ExpiriumGame.get().getClientGateway().getManager().putInvInteractPacket(InvInteractType.SWITCH_DOWN);

    public void feedHotSlots(PacketInputStream in) {
        toolSlot.setItemStack(new ItemStack(Item.getType(in.getInt()), in.getInt()));
        materialSlot.setItemStack(new ItemStack(Item.getType(in.getInt()), in.getInt()));
        consumableSlot.setItemStack(new ItemStack(Item.getType(in.getInt()), in.getInt()));
    }

    public ItemStack getItemInHand() {
        return focusedSlot.getItemStack();
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

    public Image getInventoryButton() {
        return inventoryButton;
    }

    public Image getConsumeButton() {
        return consumeButton;
    }

    public StorageGrid getInventoryGrid() {
        return inventoryGrid;
    }

    public HashMap<Integer, StorageGrid> getStorageGridIDs() {
        return storageGridIDs;
    }

}
