package com.astetyne.expirium.server.api.world.inventory;

import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.utils.Consts;

public class CookingInventory extends ExpiInventory {

    private static final float updateInterval = 1;

    private float cookingTime, updater;
    private CookingRecipe recipe;

    public CookingInventory(int rows, int columns, float maxWeight) {
        super(rows, columns, maxWeight, false);
        cookingTime = updater = 0;
    }

    public void onTick() {
        if(recipe == null) {
            label = "No recipe match";
            return;
        }
        updater += 1f / Consts.SERVER_DEFAULT_TPS;
        if(updater >= updateInterval) {
            updater = 0;
            invalid = true;
        }
        cookingTime += 1f / Consts.SERVER_DEFAULT_TPS;
        label = "Cooking: "+Math.min((int)(cookingTime * 100 / recipe.getTime()), 100)+"%";
        if(cookingTime >= recipe.time) {
            super.clear();
            super.addItem(recipe.getProduct(), true);
            invalid = true;
            recipe = null;
        }
    }

    private void matchRecipe() {
        cookingTime = 0;
        if(items.size() == 0) return;
        recipes:
        for(CookingRecipe r : CookingRecipe.values()) {
            if(r.getRequiredItems().length != items.size()) continue;
            //System.out.println("checking recipe: "+r);
            reqItems:
            for(ItemStack is : r.getRequiredItems()) {
                for(ItemStack is2 : items) {
                    if(is.getItem() == is2.getItem()) {
                        //System.out.println("item correct: "+is.getItem());
                        if(is.getAmount() != is2.getAmount()) continue recipes;
                        //System.out.println("amount correct: "+is.getAmount());
                        continue reqItems;
                    }
                }
                continue recipes;
            }
            recipe = r;
            //System.out.println("recipe success");
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
        matchRecipe();
    }

    @Override
    public void removeItemStack(ItemStack remIS) {
        super.removeItemStack(remIS);
        matchRecipe();
    }

    @Override
    public void increaseWeight(float f) {
        super.increaseWeight(f);
        matchRecipe();
    }

    @Override
    public void decreaseWeight(float f) {
        super.decreaseWeight(f);
        matchRecipe();
    }

}
