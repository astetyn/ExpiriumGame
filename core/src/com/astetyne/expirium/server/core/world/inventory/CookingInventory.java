package com.astetyne.expirium.server.core.world.inventory;

import com.astetyne.expirium.client.items.GridItemStack;
import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.server.core.world.ExpiWorld;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CookingInventory extends ExpiInventory {

    private final ExpiWorld world;
    private long startTick;
    private CookingRecipe recipe;
    private int multiply;

    public CookingInventory(ExpiWorld world, int rows, int columns, float maxWeight) {
        super(rows, columns, maxWeight, false);
        this.world = world;
        startTick = world.getTick();
        multiply = 0;
    }

    public CookingInventory(ExpiWorld world, int rows, int columns, float maxWeight, DataInputStream in) throws IOException {
        super(rows, columns, maxWeight, false, in);
        this.world = world;
        startTick = in.readLong();
        multiply = 0;
    }

    // should be called every half second = 16 ticks
    public void onCookingUpdate() {
        invalid = true;
        if(recipe == null) {
            label = "Unknown recipe";
            return;
        }

        label = "Cooking: "+Math.min((int)((-startTick + world.getTick()) * 100 / recipe.getTicks()), 100)+"%";
        if(startTick + recipe.getTicks() < world.getTick()) {
            super.clear();
            super.addItem(recipe.getProduct(), true);
            recipe = null;
        }
    }

    private void matchRecipe() {
        startTick = world.getTick();
        if(items.size() == 0) {
            recipe = null;
            return;
        }

        recipes:
        for(CookingRecipe r : CookingRecipe.values()) {
            int maximumMultiply = Integer.MAX_VALUE;

            // check if inventory has only required items = no extra items - this is important if recipes
            // share common req items
            outer:
            for(ItemStack is2 : items) {
                for(ItemStack is : r.getRequiredItems()) {
                    if(is2.getItem() == is.getItem()) continue outer;
                }
                recipe = null;
                return;
            }

            for(ItemStack is : r.getRequiredItems()) {
                int foundAmount = 0;
                for(ItemStack is2 : items) {
                    if(is2.getItem() == is.getItem()) {
                        foundAmount += is2.getAmount();
                    }
                }
                maximumMultiply = Math.min(maximumMultiply, foundAmount / is.getAmount());
                if(maximumMultiply == 0) continue recipes;
            }
            recipe = r;
            multiply = maximumMultiply;
            return;
        }
        recipe = null;
    }

    @Override
    public boolean addItem(ItemStack addIS, boolean merge) {
        boolean b = super.addItem(addIS, false);
        if(b) startTick = 0;
        matchRecipe();
        return b;
    }

    @Override
    public void removeItem(ItemStack remIS) {
        super.removeItem(remIS);
        matchRecipe();
    }

    @Override
    public void removeGridItem(GridItemStack remIS) {
        super.removeGridItem(remIS);
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

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        super.writeData(out);
        out.writeLong(startTick);
    }
}
