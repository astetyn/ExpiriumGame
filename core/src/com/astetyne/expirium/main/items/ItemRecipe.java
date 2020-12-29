package com.astetyne.expirium.main.items;

import java.util.ArrayList;
import java.util.List;

public class ItemRecipe {

    private final ItemStack product;
    private final List<ItemStack> requiredItems;
    private String description;

    public ItemRecipe(ItemStack product) {
        this.product = product;
        this.requiredItems = new ArrayList<>();
        this.description = "No description.";
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

    public void setDescription(String description) {
        this.description = description;
    }
}
