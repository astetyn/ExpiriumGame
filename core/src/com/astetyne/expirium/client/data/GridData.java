package com.astetyne.expirium.client.data;

import com.astetyne.expirium.client.items.GridItemStack;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.utils.IntVector2;
import com.astetyne.expirium.server.net.PacketInputStream;

import java.util.ArrayList;
import java.util.List;

public class GridData {

    public final List<GridItemStack> items;
    public float totalWeight, maxWeight;
    public String label;

    public GridData() {
        items = new ArrayList<>();
        label = "Your inventory";
    }

    public void feed(PacketInputStream in) {
        totalWeight = in.getFloat();
        maxWeight = in.getFloat();
        items.clear();
        int size = in.getByte();
        for(int i = 0; i < size; i++) {
            int itemID = in.getShort();
            int amount = in.getShort();
            int x = in.getByte();
            int y = in.getByte();
            items.add(new GridItemStack(Item.get(itemID), amount, new IntVector2(x, y)));
        }
    }
}
