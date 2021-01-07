package com.astetyne.expirium.server.api.world.inventory;

import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.utils.Consts;

public class CookingInventory extends ExpiInventory {

    private float cookingTime;
    private CookingRecipe recipe;

    public CookingInventory(int columns, int rows, float maxWeight) {
        super(columns, rows, maxWeight);
        cookingTime = 0;
    }

    public void onTick() {
        if(recipe == null) {
            label = "No recipe match";
            return;
        }
        cookingTime += 1f / Consts.SERVER_DEFAULT_TPS;
        label = "Cooking: "+(int)(cookingTime * 100 / recipe.getTime())+"%"; //todo: needs to be send to player
    }

    private void matchRecipe() {
        if(items.size() == 0) return;
        recipes:
        for(CookingRecipe r : CookingRecipe.values()) {
            if(r.getRequiredItems().size() != items.size()) continue;
            reqItems:
            for(ItemStack is : r.getRequiredItems()) {
                for(ItemStack is2 : items) {
                    if(is.getItem() == is2.getItem()) {
                        if(is.getAmount() != is2.getAmount()) continue recipes;
                        continue reqItems;
                    }
                }
                continue recipes;
            }
            recipe = r;
            return;
        }
        recipe = null;
    }

    @Override
    public boolean addItem(ItemStack addIS, boolean merge) {
        boolean b = super.addItem(addIS, false);
        if(b) cookingTime = 0;
        matchRecipe();
        return b;
    }

    @Override
    public void removeItem(ItemStack remIS) {
        super.removeItem(remIS);
        cookingTime = 0;
        matchRecipe();
    }

}
