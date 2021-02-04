package com.astetyne.expirium.server.core.world.inventory;

import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.items.ItemStack;

public enum CookingRecipe {

    COOKED_APPLE(10, new ItemStack[]{new ItemStack(Item.APPLE)}, new ItemStack(Item.COOKED_APPLE)),
    FRUIT_JAM(20, new ItemStack[]{new ItemStack(Item.RASPBERRY), new ItemStack(Item.APPLE), new ItemStack(Item.WOODEN_BOWL)}, new ItemStack(Item.FRUIT_JAM)),
    GLASS(5, new ItemStack[]{new ItemStack(Item.SAND, 2)}, new ItemStack(Item.GLASS));

    float time;
    ItemStack[] requiredItems;
    ItemStack product;

    CookingRecipe(float time, ItemStack[] requiredItems, ItemStack product) {
        this.time = time;
        this.requiredItems = requiredItems;
        this.product = product;
    }

    public float getTime() {
        return time;
    }

    public ItemStack[] getRequiredItems() {
        return requiredItems;
    }

    public ItemStack getProduct() {
        return product;
    }
}
