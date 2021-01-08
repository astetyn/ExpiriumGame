package com.astetyne.expirium.server.api.world.inventory;

import com.astetyne.expirium.main.items.Item;
import com.astetyne.expirium.main.items.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ExpiInventory {

    private static final HashMap<Integer, ExpiInventory> inventoriesID = new HashMap<>();

    protected final List<ItemStack> items;
    protected final int id;
    protected final ItemStack[][] grid;
    protected float totalWeight;
    protected final float maxWeight;
    protected String label;
    protected final int rows, columns;
    protected boolean withUtils;
    protected boolean invalid;

    public ExpiInventory(int rows, int columns, float maxWeight, boolean withUtils) {
        this.rows = rows;
        this.columns = columns;
        items = new ArrayList<>();
        int randomID;
        do {
            randomID = (int)(Math.random()*Integer.MAX_VALUE);
        } while(inventoriesID.containsKey(randomID));
        inventoriesID.put(randomID, this);
        id = randomID;
        grid = new ItemStack[rows][columns];
        totalWeight = 0;
        this.maxWeight = maxWeight;
        this.withUtils = withUtils;
        label = "";
        invalid = false;
    }

    /**
     * Call this method if you want to check if new item can be appended into this inventory.
     * addItem() will be successful if this method returns true.
     * @param item New item to be added.
     * @param amount Amount (for weight check).
     * @return True if item can be added, false if not.
     */
    public boolean canBeAdded(Item item, int amount) {
        if(totalWeight + item.getWeight() * amount > maxWeight) return false;
        for(ItemStack is : items) {
            if(is.getItem() == item) return true;
        }
        return isPlaceFor(item);
    }

    /**
     *
     * @param addIS item to be added
     * @return true if item was added, otherwise false
     */
    public boolean addItem(ItemStack addIS, boolean merge) {
        ItemStack copyIS = new ItemStack(addIS);

        if(merge) {
            for(ItemStack is : items) {
                if(is.getItem() == copyIS.getItem()) {
                    is.increaseAmount(copyIS.getAmount());
                    totalWeight += copyIS.getItem().getWeight() * copyIS.getAmount();
                    return true;
                }
            }
        }

        int w = copyIS.getItem().getGridWidth();
        int h = copyIS.getItem().getGridHeight();
        for(int r = rows - h; r >= 0; r--) {
            for(int c = 0; c <= columns - w; c++) {
                if(!isPlaceFor(copyIS.getItem(), r, c, -1, -1)) continue;
                copyIS.getGridPos().set(c, r);
                insertToGrid(copyIS);
                items.add(copyIS);
                totalWeight += copyIS.getItem().getWeight() * copyIS.getAmount();
                System.out.println("adding: "+copyIS+" to "+copyIS.getGridPos());
                return true;
            }
        }
        return false;
    }

    public boolean contains(Item item) {
        for(ItemStack is : items) {
            if(is.getItem() == item) return true;
        }
        return false;
    }

    public boolean contains(ItemStack conIS) {
        for(ItemStack is : items) {
            if(is.getItem() == conIS.getItem() && is.getAmount() >= conIS.getAmount()) return true;
        }
        return false;
    }

    /**
     * Removes given ItemStack from inventory. Given ItemStack must be already in the inventory. (this method is not safe)
     * @param remIS
     */
    public void removeItemStack(ItemStack remIS) {
        System.out.println("removing: "+remIS+" from "+remIS.getGridPos());
        items.remove(remIS);
        cleanGridFrom(remIS);
        totalWeight -= remIS.getItem().getWeight() * remIS.getAmount();
    }

    /**
     * This method will try to remove required amount of items from the inventory.
     * Note that this is not possible if there is bigger amount in given ItemStack than in whole inventory for that item.
     * In that case, whole amount from inventory will be removed.
     * @param remIS
     */
    public void removeItem(ItemStack remIS) {
        int removedAmount = 0;
        Iterator<ItemStack> it = items.iterator();
        while(it.hasNext()) {
            ItemStack is = it.next();
            if(is.getItem() == remIS.getItem()) {
                int toBeRemoved = Math.min(is.getAmount(), remIS.getAmount() - removedAmount);
                removedAmount += toBeRemoved;
                is.decreaseAmount(toBeRemoved);
                if(is.getAmount() == 0) {
                    it.remove();
                    cleanGridFrom(is);
                }
                if(removedAmount == remIS.getAmount()) {
                    totalWeight -= remIS.getItem().getWeight() * removedAmount;
                    return;
                }
            }
        }
        // remove was not completely successful
        totalWeight -= remIS.getItem().getWeight() * removedAmount;
    }

    public void clear() {
        for(ItemStack is : items) {
            cleanGridFrom(is);
        }
        totalWeight = 0;
        items.clear();
    }

    public void insertToGrid(ItemStack is) {
        for(int i = 0; i < is.getItem().getGridHeight(); i++) {
            for(int j = 0; j < is.getItem().getGridWidth(); j++) {
                grid[is.getGridPos().y+i][is.getGridPos().x+j] = is;
            }
        }
    }

    public void cleanGridFrom(ItemStack is) {
        for(int i = 0; i < is.getItem().getGridHeight(); i++) {
            for(int j = 0; j < is.getItem().getGridWidth(); j++) {
                grid[is.getGridPos().y+i][is.getGridPos().x+j] = null;
            }
        }
    }

    /**
     *
     * @param item Item to be checked.
     * @return True if item can be appended to this inventory. (does not check weight)
     */
    public boolean isPlaceFor(Item item) {
        int w = item.getGridWidth();
        int h = item.getGridHeight();
        for(int i = rows - h; i >= 0; i--) {
            for(int j = 0; j <= columns - w; j++) {
                if(isPlaceFor(item, i, j, -1, -1)) return true;
            }
        }
        return false;
    }

    /**
     * This method will tell you if there is a clean place for given item to be added into this inventory.
     * @param item item to be checked
     * @param r row of the place
     * @param c column of the place
     * @param pr previous row (if item was not in the inventory, set to -1)
     * @param pc previous column (if item was not in the inventory, set to -1)
     * @return true if the place is clean, otherwise false
     */
    public boolean isPlaceFor(Item item, int r, int c, int pr, int pc) {
        int w = item.getGridWidth();
        int h = item.getGridHeight();
        ItemStack prevIS = null;
        if(pr != -1) prevIS = grid[pr][pc];
        if(r < 0 || r + h > rows || h < 0 || c + w > columns) return false;
        for(int i = 0; i < h; i++) {
            for(int j = 0; j < w; j++) {
                if((withUtils  && r+i == 0 && (c+j == columns-1 || c+j == columns-2)) || grid[r+i][c+j] != null && grid[r+i][c+j] != prevIS) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isInside(int r, int c) {
        return r < rows && c < columns; // aj tak tu chyba 0
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public float getTotalWeight() {
        return totalWeight;
    }

    public float getMaxWeight() {
        return maxWeight;
    }

    public ItemStack[][] getGrid() {
        return grid;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public void increaseWeight(float f) {
        totalWeight += f;
    }

    public void decreaseWeight(float f) {
        totalWeight -= f;
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }
}
