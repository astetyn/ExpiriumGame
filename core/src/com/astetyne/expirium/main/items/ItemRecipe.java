package com.astetyne.expirium.main.items;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public enum ItemRecipe {

    PICKAXE(new ItemStack(Item.PICKAXE), Arrays.asList(new ItemStack(Item.RAW_WOOD), new ItemStack(Item.RAW_WOOD), new ItemStack(Item.RAW_WOOD)), "lol");

    private static final HashMap<Integer, ItemRecipe> map;
    static {
        map = new HashMap<>();
        int i = 0;
        for(ItemRecipe it : ItemRecipe.values()) {
            it.id = i;
            map.put(it.id, it);
        }
    }

    int id;
    ItemStack product;
    List<ItemStack> requiredItems;
    String description;

    ItemRecipe(ItemStack product, List<ItemStack> reqItems, String desc) {
        this.product = product;
        this.requiredItems = reqItems;
        this.description = desc;
    }

    public int getId() {
        return id;
    }

    public ItemStack getProduct() {
        return product;
    }

    public List<ItemStack> getRequiredItems() {
        return requiredItems;
    }

    public String getDescription() {
        return description;
    }

    public static ItemRecipe getRecipe(int id) {
        return map.get(id);
    }
}
