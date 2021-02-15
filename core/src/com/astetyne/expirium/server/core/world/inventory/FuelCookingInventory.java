package com.astetyne.expirium.server.core.world.inventory;

import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.client.utils.IntVector2;
import com.astetyne.expirium.server.core.world.ExpiWorld;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;

import java.io.DataInputStream;
import java.io.IOException;

public class FuelCookingInventory extends CookingInventory {

    private int fuel;
    private long lastFuelDecreaseTick;

    public FuelCookingInventory(ExpiWorld world, int rows, int columns, float maxWeight) {
        super(world, rows, columns, maxWeight);
        fuel = 0;
        lastFuelDecreaseTick = world.getTick();
    }

    public FuelCookingInventory(ExpiWorld world, int rows, int columns, float maxWeight, DataInputStream in) throws IOException {
        super(world, rows, columns, maxWeight, in);
        fuel = in.readInt();
        lastFuelDecreaseTick = world.getTick();
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
    protected void generateLabel() {
        if(recipe == null) {
            label = "[Fuel: " + fuel + "] Unknown recipe";
        }else {
            label = "[Fuel: " + fuel + "] Cooking: " + Math.min((int) ((-startTick + world.getTick()) * 100 / recipe.getTicks()), 100) + "%";
        }
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
    public void append(Item item, int amount) {
        if(item == Item.COAL) {
            increaseFuel(10 * amount);
            refresh();
            return;
        }else if(item == Item.RAW_WOOD) {
            increaseFuel(2 * amount);
            refresh();
            return;
        }
        super.append(item, amount);
    }

    @Override
    public void insert(ItemStack is, IntVector2 pos) {
        if(is.getItem() == Item.COAL) {
            increaseFuel(10 * is.getAmount());
            refresh();
            return;
        }else if(is.getItem() == Item.RAW_WOOD) {
            increaseFuel(2 * is.getAmount());
            refresh();
            return;
        }
        super.insert(is, pos);
    }

    private void decreaseFuel(int i) {
        fuel = Math.max(fuel - i, 0);
    }

    private void increaseFuel(int i) {
        fuel = Math.min(fuel + i, 100);
        lastFuelDecreaseTick = world.getTick();
    }

    public int getFuel() {
        return fuel;
    }
}
