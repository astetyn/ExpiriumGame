package com.astetyne.expirium.server.core.world.inventory;

import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.server.core.world.World;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;

import java.io.DataInputStream;
import java.io.IOException;

public class CookingInventory extends Inventory {

    protected final World world;
    protected long startTick;
    protected CookingRecipe recipe;
    private int multiply;

    public CookingInventory(World world, int rows, int columns, float maxWeight) {
        super(rows, columns, maxWeight);
        this.world = world;
        startTick = world.getTick();
        multiply = 0;
    }

    public CookingInventory(World world, int rows, int columns, float maxWeight, DataInputStream in) throws IOException {
        super(rows, columns, maxWeight, in);
        this.world = world;
        startTick = in.readLong();
        multiply = 0;
    }

    // should be called every half second = 16 ticks
    public void onCookingUpdate() {
        willNeedUpdate();
        generateLabel();
        if(recipe == null) return;

        if(startTick + recipe.getTicks() < world.getTick()) {
            CookingRecipe currRecipe = recipe;
            recipe = null;
            clear(); // this will null recipe anyways
            append(currRecipe.getProduct().getItem(), currRecipe.getProduct().getAmount()*multiply);
        }
    }

    protected void generateLabel() {
        if(recipe == null) {
            label = "Unknown recipe";
        }else {
            label = "Cooking: " + Math.min((int) ((-startTick + world.getTick()) * 100 / recipe.getTicks()), 100) + "%";
        }
    }

    protected void matchRecipe() {

        startTick = world.getTick();
        if(items.size() == 0) {
            recipe = null;
            return;
        }

        recipes:
        for(CookingRecipe r : CookingRecipe.values()) {
            int mtp = -1;

            // check if inventory has only required items = no extra items - this is important if recipes
            // share common req items
            outer:
            for(ItemStack is2 : items) {
                for(ItemStack is : r.getRequiredItems()) {
                    if(is2.getItem() == is.getItem()) continue outer;
                }
                continue recipes;
            }

            for(ItemStack is : r.getRequiredItems()) {
                int foundAmount = 0;
                for(ItemStack is2 : items) {
                    if(is2.getItem() == is.getItem()) {
                        foundAmount += is2.getAmount();
                    }
                }
                int mtpFound = foundAmount / is.getAmount();
                if(mtpFound == 0 || (mtp != -1 && mtpFound != mtp) || foundAmount % is.getAmount() != 0) continue recipes;
                mtp = mtpFound;
            }
            recipe = r;
            multiply = mtp;
            return;
        }
        recipe = null;
    }

    @Override
    public void refresh() {
        super.refresh();
        matchRecipe();
    }

    @Override
    public void writeData(WorldBuffer out) {
        super.writeData(out);
        out.writeLong(startTick);
    }
}
