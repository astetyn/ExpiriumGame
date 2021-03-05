package com.astetyne.expirium.server.core.world.inventory;

import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.items.ItemStack;

public enum RecycleRecipe {

    RECIPE_1(new ItemStack[]{new ItemStack(Item.RASPBERRY, 10)}),
    RECIPE_2(new ItemStack[]{new ItemStack(Item.BLUEBERRY, 10)}),
    RECIPE_3(new ItemStack[]{new ItemStack(Item.SAND, 20)}),
    RECIPE_4(new ItemStack[]{new ItemStack(Item.DIRT, 20)}),
    RECIPE_5(new ItemStack[]{new ItemStack(Item.LIMESTONE, 10)}),
    RECIPE_6(new ItemStack[]{new ItemStack(Item.RAW_WOOD, 5)}),
    ;

    private final ItemStack[] requiredItems;

    RecycleRecipe(ItemStack[] requiredItems) {
        this.requiredItems = requiredItems;
    }

    public ItemStack[] getRequiredItems() {
        return requiredItems;
    }

}
