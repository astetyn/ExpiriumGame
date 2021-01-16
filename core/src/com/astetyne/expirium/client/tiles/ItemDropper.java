package com.astetyne.expirium.client.tiles;

import com.astetyne.expirium.client.items.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemDropper {

    public final Item[] items;
    public final float[] chances;

    public ItemDropper(Item[] items, float... chances) {
        this.items = items;
        this.chances = chances;
    }

    public static List<Item> chooseItems(ItemDropper dropper) {

        List<Item> chosenItems = new ArrayList<>();

        double rand = Math.random();

        for(int i = 0; i < dropper.items.length; i++) {
            if(dropper.chances[i] > rand) {
                chosenItems.add(dropper.items[i]);
            }
        }
        return chosenItems;
    }
}
