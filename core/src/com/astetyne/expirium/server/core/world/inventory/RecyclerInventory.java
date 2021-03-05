package com.astetyne.expirium.server.core.world.inventory;

import com.astetyne.expirium.client.data.InvVariableType;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.core.world.World;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.astetyne.expirium.server.net.PacketOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

public class RecyclerInventory extends Inventory {

    private static final InvVariableType[] variables = new InvVariableType[]{InvVariableType.RECYCLE};
    private static final int duration = Consts.SERVER_TPS * 60;

    protected final World world;
    private String recycleVariable;
    protected long startTick;
    private int multiply;

    public RecyclerInventory(World world, int rows, int columns, float maxWeight) {
        super(rows, columns, maxWeight);
        this.world = world;
        recycleVariable = "";
        startTick = 0;
        multiply = 0;
    }

    public RecyclerInventory(World world, int rows, int columns, float maxWeight, DataInputStream in) throws IOException {
        super(rows, columns, maxWeight, in);
        this.world = world;
        recycleVariable = "";
        startTick = in.readLong();
        multiply = 0;
    }

    public void update() {
        willNeedUpdate();
        if(multiply == 0) {
            recycleVariable = "-";
        }else {
            recycleVariable = Math.min((int) ((-startTick + world.getTick()) * 100 / duration), 100) + "%";

            if(startTick + duration < world.getTick()) {
                int oldMlt = multiply;
                clear();
                append(Item.NATURAL_MIX, oldMlt);
            }
        }
    }

    @Override
    public void refresh() {
        super.refresh();
        matchRecipe();
    }

    protected void matchRecipe() {

        startTick = world.getTick();
        if(items.size() == 0) {
            multiply = 0;
            return;
        }

        recipes:
        for(RecycleRecipe r : RecycleRecipe.values()) {
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
            multiply = mtp;
            return;
        }
        multiply = 0;
    }

    @Override
    public String getLabel() {
        return "Recycler";
    }

    @Override
    public void writeVariablesData(PacketOutputStream out) {
        out.putShortString(recycleVariable);
    }

    @Override
    public InvVariableType[] getVariables() {
        return variables;
    }

    @Override
    public void writeData(WorldBuffer out) {
        super.writeData(out);
        out.writeLong(startTick);
    }
}
