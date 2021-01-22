package com.astetyne.expirium.client.items;

import java.util.HashMap;

public enum ItemRecipe {

    WOODEN_MATTOCK(Item.WOODEN_MATTOCK, new ItemStack[]{new ItemStack(Item.STONE, 2), new ItemStack(Item.RAW_WOOD, 30)}, "Better than nothing."),
    RHYOLITE_MATTOCK(Item.RHYOLITE_MATTOCK, new ItemStack[]{new ItemStack(Item.RHYOLITE, 10), new ItemStack(Item.RAW_WOOD, 20)}, "Quite strong for soft materials."),
    WOODEN_WALL(Item.WOODEN_WALL, 2, new ItemStack[]{new ItemStack(Item.RAW_WOOD, 10)}, "Good for basic house walls. High stability included."),
    SOFT_WOODEN_WALL(Item.SOFT_WOODEN_WALL, 2, new ItemStack[]{new ItemStack(Item.RAW_WOOD, 10)}, "Same as wooden wall but softer to your body."),
    WOODEN_SUPPORT(Item.WOODEN_SUPPORT, 6, new ItemStack[]{new ItemStack(Item.RAW_WOOD, 6)}, "Cheap and useful for maintaining high stability."),
    CAMPFIRE(Item.CAMPFIRE, new ItemStack[]{new ItemStack(Item.RAW_WOOD, 15)}, "Make it if you are cold. It will warm your small house."),
    BOWL(Item.WOODEN_BOWL, new ItemStack[]{new ItemStack(Item.RAW_WOOD, 10)}, "toto ma byt akoze miska? hahaha");

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
