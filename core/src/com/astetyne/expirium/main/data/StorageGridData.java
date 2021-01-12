package com.astetyne.expirium.main.data;

import com.astetyne.expirium.main.items.Item;
import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.utils.IntVector2;
import com.astetyne.expirium.server.backend.PacketInputStream;

import java.util.ArrayList;
import java.util.List;

public class StorageGridData {

    public final List<ItemStack> items;
    public int rows, columns;
    public float totalWeight, maxWeight;
    public String label;

    public StorageGridData() {
        items = new ArrayList<>();
        rows = 1;
        columns = 1;
        label = "";
    }

    public void feed(PacketInputStream in) {
        label = in.getString();
        totalWeight = in.getFloat();
        maxWeight = in.getFloat();
        items.clear();
        int itemsNumber = in.getInt();
        for(int i = 0; i < itemsNumber; i++) {
            int itemID = in.getInt();
            int amount = in.getInt();
            IntVector2 pos = in.getIntVector();
            items.add(new ItemStack(Item.getType(itemID), amount, pos));
        }
    }
}
