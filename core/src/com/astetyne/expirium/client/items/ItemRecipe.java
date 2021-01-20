package com.astetyne.expirium.client.items;

import java.util.HashMap;

public enum ItemRecipe {

    RHYOLITE_PICKAXE(Item.RHYOLITE_PICKAXE, new ItemStack[]{new ItemStack(Item.RHYOLITE, 5), new ItemStack(Item.RAW_WOOD, 20)}, "Palko je naj."),
    WOODEN_WALL(Item.WOODEN_WALL, 2, new ItemStack[]{new ItemStack(Item.RAW_WOOD, 10)}, "Nepriestrelna stena, naozaj."),
    CAMPFIRE(Item.CAMPFIRE, new ItemStack[]{new ItemStack(Item.RAW_WOOD, 15)}, "Ohnicek, ktory aj zahreje."),
    BOWL(Item.WOODEN_BOWL, new ItemStack[]{new ItemStack(Item.RAW_WOOD, 10)}, "toto ma byt akoze miska?");

    ItemStack product;
    ItemStack[] requiredItems;
    String description;

    ItemRecipe(Item product, ItemStack[] reqItems, String desc) {
        this.product = new ItemStack(product);
        this.requiredItems = reqItems;
        this.description = desc;
    }

    ItemRecipe(Item product, int amount, ItemStack[] reqItems, String desc) {
        this.product = new ItemStack(product, amount);
        this.requiredItems = reqItems;
        this.description = desc;
    }

    public int getId() {
        return id;
    }

    public ItemStack getProduct() {
        return product;
    }

    public ItemStack[] getRequiredItems() {
        return requiredItems;
    }

    public String getDescription() {
        return description;
    }

    public static ItemRecipe getRecipe(int id) {
        return map.get(id);
    }

    int id;
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
}
