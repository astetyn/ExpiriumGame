package com.astetyne.expirium.server.api.world.inventory;

import com.astetyne.expirium.main.items.Item;
import com.astetyne.expirium.main.items.ItemCategory;
import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.server.api.entity.ExpiPlayer;

import java.util.ArrayList;
import java.util.List;

public class ExpiPlayerInventory extends ExpiInventory {

    private final ExpiPlayer owner;
    private ItemStack itemInHand;
    private int indexTools, indexMats, indexCons;
    private ItemStack isTool, isMat, isCon;
    private ChosenSlot chosenSlot;

    public ExpiPlayerInventory(ExpiPlayer owner, int columns, int rows, float maxWeight) {
        super(rows, columns, maxWeight, true);
        this.owner = owner;
        indexTools = 0;
        indexMats = 0;
        indexCons = 0;
        isTool = new ItemStack(Item.EMPTY);
        isMat = new ItemStack(Item.EMPTY);
        isCon = new ItemStack(Item.EMPTY);
        chosenSlot = ChosenSlot.TOOL_SLOT;
        itemInHand = new ItemStack(Item.EMPTY);
        label = "This is better than minecraft.";
    }

    public void onInteract(UIInteractType type) {

        switch(type) {

            case SLOT_TOOLS: chosenSlot = ChosenSlot.TOOL_SLOT; break;
            case SLOT_MATERIALS: chosenSlot = ChosenSlot.MATERIAL_SLOT; break;
            case SLOT_CONSUMABLE: chosenSlot = ChosenSlot.CONSUMABLE_SLOT; break;
            case SWITCH_UP:
                if(itemInHand.getItem().getCategory() == ItemCategory.TOOL) {
                    indexTools++;
                }else if(itemInHand.getItem().getCategory() == ItemCategory.MATERIAL){
                    indexMats++;
                }else if(itemInHand.getItem().getCategory() == ItemCategory.CONSUMABLE){
                    indexCons++;
                }
                break;
            case SWITCH_DOWN:
                if(itemInHand.getItem().getCategory() == ItemCategory.TOOL) {
                    indexTools--;
                }else if(itemInHand.getItem().getCategory() == ItemCategory.MATERIAL){
                    indexMats--;
                }else if(itemInHand.getItem().getCategory() == ItemCategory.CONSUMABLE){
                    indexCons--;
                }
                break;
            case OPEN_INV:
                owner.getNetManager().putInvFeedPacket(); //todo: toto je zbytocne?
                break;
            case CONSUME_BUTTON:
                if(itemInHand.getItem().getCategory() != ItemCategory.CONSUMABLE) break;

                switch(itemInHand.getItem()) {
                    case APPLE:
                        owner.increaseFoodLevel(10);
                        break;
                    case COOKED_APPLE:
                        owner.increaseFoodLevel(15);
                        break;
                }

                break;
        }
        updateHotSlots();

    }

    private void updateHotSlots() {

        List<ItemStack> is1 = new ArrayList<>();
        List<ItemStack> is2 = new ArrayList<>();
        List<ItemStack> is3 = new ArrayList<>();

        for(ItemStack is : items) {
            if(is.getItem().getCategory() == ItemCategory.TOOL) {
                is1.add(is);
            }else if(is.getItem().getCategory() == ItemCategory.MATERIAL) {
                is2.add(is);
            }else if(is.getItem().getCategory() == ItemCategory.CONSUMABLE) {
                is3.add(is);
            }
        }

        if(indexTools >= is1.size()) {
            indexTools = 0;
        }else if(indexTools <= -1) {
            indexTools = is1.size() - 1;
        }
        if(indexMats >= is2.size()) {
            indexMats = 0;
        }else if(indexMats <= -1) {
            indexMats = is2.size() - 1;
        }
        if(indexCons >= is3.size()) {
            indexCons = 0;
        }else if(indexCons <= -1) {
            indexCons = is3.size() - 1;
        }

        if(is1.size() == 0) {
            isTool = new ItemStack(Item.EMPTY);
        }else {
            isTool = is1.get(indexTools);
        }
        if(is2.size() == 0) {
            isMat = new ItemStack(Item.EMPTY);
        }else {
            isMat = is2.get(indexMats);
        }
        if(is3.size() == 0) {
            isCon = new ItemStack(Item.EMPTY);
        }else {
            isCon = is3.get(indexCons);
        }

        if(chosenSlot == ChosenSlot.TOOL_SLOT) {
            itemInHand = isTool;
        }else if(chosenSlot == ChosenSlot.MATERIAL_SLOT) {
            itemInHand = isMat;
        }else if(chosenSlot == ChosenSlot.CONSUMABLE_SLOT) {
            itemInHand = isCon;
        }

        owner.getNetManager().putHotSlotsFeedPacket((byte) chosenSlot.getId(), isTool, isMat, isCon);

    }

    @Override
    public boolean addItem(ItemStack addIS, boolean merge) {
        boolean b = super.addItem(addIS, merge);
        updateHotSlots();
        return b;
    }

    @Override
    public void removeItem(ItemStack remIS) {
        super.removeItem(remIS);
        updateHotSlots();
    }

    public ItemStack getItemInHand() {
        return itemInHand;
    }
}
