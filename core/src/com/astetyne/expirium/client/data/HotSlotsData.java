package com.astetyne.expirium.client.data;

import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.server.api.world.inventory.ChosenSlot;
import com.astetyne.expirium.server.net.PacketInputStream;

public class HotSlotsData {

    private ChosenSlot chosenSlot;
    private ItemStack is1, is2, is3;

    public HotSlotsData() {
        chosenSlot = ChosenSlot.TOOL_SLOT;
        is1 = new ItemStack(Item.EMPTY);
        is2 = new ItemStack(Item.EMPTY);
        is3 = new ItemStack(Item.EMPTY);
    }

    public void feed(PacketInputStream in) {
        chosenSlot = ChosenSlot.getSlot(in.getByte());
        is1 = new ItemStack(Item.getType(in.getInt()), in.getInt());
        is2 = new ItemStack(Item.getType(in.getInt()), in.getInt());
        is3 = new ItemStack(Item.getType(in.getInt()), in.getInt());
    }

    public ChosenSlot getChosenSlot() {
        return chosenSlot;
    }

    public ItemStack getIs1() {
        return is1;
    }

    public ItemStack getIs2() {
        return is2;
    }

    public ItemStack getIs3() {
        return is3;
    }
}
