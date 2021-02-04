package com.astetyne.expirium.server.core.world.inventory;

import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.items.ItemCat;
import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.server.core.Saveable;
import com.astetyne.expirium.server.core.entity.ExpiPlayer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExpiPlayerInventory extends ExpiInventory implements Saveable {

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

    public ExpiPlayerInventory(ExpiPlayer owner, int columns, int rows, float maxWeight, DataInputStream in) throws IOException {
        super(rows, columns, maxWeight, true, in);
        this.owner = owner;
        indexTools = in.readInt();
        indexMats = in.readInt();
        indexCons = in.readInt();
        isTool = new ItemStack(Item.getType(in.readInt()));
        isMat = new ItemStack(Item.getType(in.readInt()));
        isCon = new ItemStack(Item.getType(in.readInt()));
        chosenSlot = ChosenSlot.getSlot(in.readInt());
        itemInHand = new ItemStack(Item.getType(in.readInt()));
        label = "This is better than minecraft.";
    }

    public void onInteract(UIInteractType type) {

        switch(type) {

            case SLOT_TOOLS: chosenSlot = ChosenSlot.TOOL_SLOT; break;
            case SLOT_MATERIALS: chosenSlot = ChosenSlot.MATERIAL_SLOT; break;
            case SLOT_CONSUMABLE: chosenSlot = ChosenSlot.CONSUMABLE_SLOT; break;
            case SWITCH_UP:
                if(itemInHand.getItem().getCategory() == ItemCat.TOOL) {
                    indexTools++;
                }else if(itemInHand.getItem().getCategory() == ItemCat.MATERIAL){
                    indexMats++;
                }else if(itemInHand.getItem().getCategory() == ItemCat.CONSUMABLE){
                    indexCons++;
                }
                break;
            case SWITCH_DOWN:
                if(itemInHand.getItem().getCategory() == ItemCat.TOOL) {
                    indexTools--;
                }else if(itemInHand.getItem().getCategory() == ItemCat.MATERIAL){
                    indexMats--;
                }else if(itemInHand.getItem().getCategory() == ItemCat.CONSUMABLE){
                    indexCons--;
                }
                break;
            case OPEN_INV:
                owner.getNetManager().putInvFeedPacket(); //todo: toto je zbytocne?
                break;
            case CONSUME_BUTTON:
                if(itemInHand.getItem().getCategory() != ItemCat.CONSUMABLE) break;

                switch(itemInHand.getItem()) {
                    case APPLE:
                        owner.increaseFoodLevel(10);
                        removeItem(Item.APPLE);
                        break;

                    case COOKED_APPLE:
                        owner.increaseFoodLevel(15);
                        removeItem(Item.COOKED_APPLE);
                        break;

                    case RASPBERRY:
                        owner.increaseFoodLevel(5);
                        removeItem(Item.RASPBERRY);
                        break;

                    case FRUIT_JAM:
                        owner.increaseFoodLevel(25);
                        removeItem(Item.FRUIT_JAM);
                        addItem(new ItemStack(Item.WOODEN_BOWL), true);
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
            if(is.getItem().getCategory() == ItemCat.TOOL) {
                is1.add(is);
            }else if(is.getItem().getCategory() == ItemCat.MATERIAL) {
                is2.add(is);
            }else if(is.getItem().getCategory() == ItemCat.CONSUMABLE) {
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

        owner.getNetManager().putHotSlotsFeedPacket(chosenSlot, isTool, isMat, isCon);

        for(ExpiPlayer ep : owner.getServer().getPlayers()) {
            ep.getNetManager().putHandItemPacket(owner.getId(), itemInHand.getItem());
        }

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

    @Override
    public void clear() {
        super.clear();
        updateHotSlots();
        owner.getNetManager().putInvFeedPacket();
    }

    public ItemStack getItemInHand() {
        return itemInHand;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        super.writeData(out);
        out.writeInt(indexTools);
        out.writeInt(indexMats);
        out.writeInt(indexCons);
        out.writeInt(isTool.getItem().getId());
        out.writeInt(isMat.getItem().getId());
        out.writeInt(isCon.getItem().getId());
        out.writeInt(chosenSlot.getId());
        out.writeInt(itemInHand.getItem().getId());
    }
}
