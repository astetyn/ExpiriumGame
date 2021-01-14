package com.astetyne.expirium.server.api.world.inventory;

import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.items.ItemStack;

public enum CookingRecipe {

    COOKED_APPLE(10, new ItemStack[]{new ItemStack(Item.GRASS), new ItemStack(Item.DIRT)}, new ItemStack(Item.STONE, 5));

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
