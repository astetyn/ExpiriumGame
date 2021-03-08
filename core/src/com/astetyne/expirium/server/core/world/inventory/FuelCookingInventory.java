package com.astetyne.expirium.server.core.world.inventory;

import com.astetyne.expirium.client.data.ExtraCell;
import com.astetyne.expirium.client.data.ExtraCellTexture;
import com.astetyne.expirium.client.data.InvVariableType;
import com.astetyne.expirium.client.items.GridItemStack;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.client.utils.IntVector2;
import com.astetyne.expirium.server.core.world.World;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.astetyne.expirium.server.net.PacketOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

public class FuelCookingInventory extends CookingInventory {

    private static final InvVariableType[] variables = new InvVariableType[]{InvVariableType.STOPWATCH, InvVariableType.COAL_FUEL};

    private int fuel;
    private long lastFuelDecreaseTick;
    private String fuelVariable;

    public FuelCookingInventory(World world, int rows, int columns, float maxWeight) {
        super(world, rows, columns, maxWeight);
        fuel = 0;
        lastFuelDecreaseTick = world.getTick();
        fuelVariable = "";
    }

    public FuelCookingInventory(World world, int rows, int columns, float maxWeight, DataInputStream in) throws IOException {
        super(world, rows, columns, maxWeight, in);
        fuel = in.readInt();
        lastFuelDecreaseTick = world.getTick();
        fuelVariable = "";
    }

    @Override
    public void writeData(WorldBuffer out) {
        super.writeData(out);
        out.writeInt(fuel);
    }

    @Override
    public void onCookingUpdate() {
        super.onCookingUpdate();
        if(fuel == 0) {
            recipe = null;
            return;
        }
        int fuelDecrease = (int) ((world.getTick() - lastFuelDecreaseTick)/64);
        decreaseFuel(fuelDecrease);
        if(fuelDecrease > 0) {
            lastFuelDecreaseTick = world.getTick();
        }
    }

    @Override
    protected void updateVariablesText() {
        super.updateVariablesText();
        fuelVariable = fuel+"";
    }

    @Override
    protected void matchRecipe() {
        if(fuel == 0) {
            recipe = null;
            return;
        }
        super.matchRecipe();
    }

    @Override
    protected boolean canInsert(Item item, int x, int y) {
        if((getFuel(item) == 0 && y == 0 && x + item.getGridWidth() >= columns)) return false;
        return super.canInsert(item, x, y);
    }

    @Override
    public void move(IntVector2 pos1, IntVector2 pos2) {
        GridItemStack gis = grid[pos1.x][pos1.y];
        if(gis == null) return;
        if(pos2.y == 0 && pos2.x == columns-1) return;
        super.move(pos1, pos2);
    }

    @Override
    public void insert(ItemStack is, IntVector2 pos) {
        if(pos.y == 0 && pos.x == columns-1) {
            increaseFuel(getFuel(is.getItem()) * is.getAmount());
            willNeedUpdate();
            return;
        }
        super.insert(is, pos);
    }

    private void decreaseFuel(int i) {
        fuel = Math.max(fuel - i, 0);
    }

    private void increaseFuel(int i) {
        fuel += i;
        lastFuelDecreaseTick = world.getTick();
        if(recipe == null) {
            matchRecipe();
        }
    }

    public int getFuel() {
        return fuel;
    }

    private int getFuel(Item item) {
        switch(item) {
            case COAL: return 30;
            case PLANKS:
            case WOODEN_WALL:
                return 10;
            case RAW_WOOD: return 5;
            case DRY_LEAVES: return 1;
            case WOODEN_MATTOCK:
            case WOODEN_BOWL:
                return 2;
            default: return 0;
        }
    }

    @Override
    public InvVariableType[] getVariables() {
        return variables;
    }

    @Override
    public void writeVariablesData(PacketOutputStream out) {
        super.writeVariablesData(out);
        out.putShortString(fuelVariable);
    }

    @Override
    public ExtraCell[] getExtraCells() {
        return new ExtraCell[]{new ExtraCell(columns-1, 0, ExtraCellTexture.COAL_FUEL)};
    }
}
