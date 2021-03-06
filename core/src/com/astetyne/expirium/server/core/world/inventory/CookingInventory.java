package com.astetyne.expirium.server.core.world.inventory;

import com.astetyne.expirium.client.data.InvVariableType;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.server.core.world.World;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.astetyne.expirium.server.net.PacketOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

public class CookingInventory extends Inventory {

    private static final InvVariableType[] variables = new InvVariableType[]{InvVariableType.STOPWATCH};

    protected final World world;
    protected long startTick;
    protected CookingRecipe recipe;
    private int multiply;
    private String timeVariable;

    public CookingInventory(World world, int rows, int columns, float maxWeight) {
        super(rows, columns, maxWeight);
        this.world = world;
        startTick = world.getTick();
        multiply = 0;
        timeVariable = "";
    }

    public CookingInventory(World world, int rows, int columns, float maxWeight, DataInputStream in) throws IOException {
        super(rows, columns, maxWeight, in);
        this.world = world;
        startTick = in.readLong();
        multiply = 0;
        timeVariable = "";
    }

    // should be called every half second = 16 ticks
    public void onCookingUpdate() {
        willNeedUpdate();
        updateVariablesText();
        if(recipe == null) return;

        if(startTick + recipe.getTicks() < world.getTick()) {
            CookingRecipe currRecipe = recipe;
            recipe = null;
            clear(); // this will null recipe anyways
            append(currRecipe.getProduct().getItem(), currRecipe.getProduct().getAmount()*multiply);

            //this is only for this case - will need to by generalized
            if(currRecipe.getProduct().getItem() == Item.MEAT_SOUP) {
                append(Item.BUCKET, multiply);
            }
        }
    }

    protected void updateVariablesText() {
        if(recipe == null) {
            timeVariable = "-";
        }else {
            timeVariable = Math.min((int) ((-startTick + world.getTick()) * 100 / recipe.getTicks()), 100) + "%";
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
    public String getLabel() {
        return "Cooking fire";
    }

    @Override
    public InvVariableType[] getVariables() {
        return variables;
    }

    @Override
    public void writeVariablesData(PacketOutputStream out) {
        out.putShortString(timeVariable);
    }

    @Override
    public void writeData(WorldBuffer out) {
        super.writeData(out);
        out.writeLong(startTick);
    }
}
