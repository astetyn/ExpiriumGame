package com.astetyne.expirium.server.api.world.inventory;

import com.astetyne.expirium.main.items.ItemStack;

import java.util.ArrayList;
import java.util.List;

public enum CookingRecipe {

    COOKED_APPLE(10, new ArrayList<>());

    float time;
    List<ItemStack> requiredItems;

    CookingRecipe(float time, List<ItemStack> requiredItems) {
        this.time = time;
        this.requiredItems = requiredItems;
    }

    public float getTime() {
        return time;
    }

    public List<ItemStack> getRequiredItems() {
        return requiredItems;
    }
}
