package com.astetyne.expirium.main.items;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public enum ItemRecipe {

    PICKAXE(new ItemStack(Item.PICKAXE), Arrays.asList(new ItemStack(Item.RAW_WOOD, 10), new ItemStack(Item.STONE, 3)), "Palko je naj."),
    WOODEN_WALL(new ItemStack(Item.WOODEN_WALL, 3), Arrays.asList(new ItemStack(Item.RAW_WOOD, 1)), "Nepriestrelna stena."),
    CAMPFIRE(new ItemStack(Item.CAMPFIRE), Arrays.asList(new ItemStack(Item.RAW_WOOD, 1)), "Ohnicek, ktory aj zahreje.");

    private static final HashMap<Integer, ItemRecipe> map;
    static {
        map = new HashMap<>();
        int i = 0;
        for(ItemRecipe it : ItemRecipe.values()) {
            it.id = i;
            map.put(it.id, it);
            i++;
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
