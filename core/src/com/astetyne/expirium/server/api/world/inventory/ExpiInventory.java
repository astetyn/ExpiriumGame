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

    public ExpiInventory(int columns, int rows, float maxWeight) {
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
        label = "";
    }

    public boolean canBeAdded(Item item, int amount) {
        if(totalWeight + item.getWeight() * amount > maxWeight) return false;

        //todo: skontrolovat miesto

        return true;
    }

    public boolean canBeAdded(ItemStack is, int r, int c) {
        if(!isPlaceFor(is, c, r)) return false;
        return !(totalWeight + is.getItem().getWeight() * is.getAmount() > maxWeight);
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
        for(int i = grid.length - h; i >= 0; i--) {
            for(int j = 0; j <= grid[0].length - w; j++) {
                if(!isPlaceFor(copyIS, j, i)) continue;
                copyIS.getGridPos().set(j, i);
                insertToGrid(copyIS);
                items.add(copyIS);
                totalWeight += copyIS.getItem().getWeight() * copyIS.getAmount();
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

    public void removeItem(ItemStack remIS) {
        Iterator<ItemStack> it = items.iterator();
        while(it.hasNext()) {
            ItemStack is = it.next();
            if(is.getItem() == remIS.getItem()) {
                totalWeight -= is.getItem().getWeight() * Math.min(is.getAmount(), remIS.getAmount());
                is.decreaseAmount(remIS.getAmount());
                if(is.getAmount() <= 0) {
                    it.remove();
                    cleanGridFrom(is);
                    break;
                }
            }
        }
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

    private boolean isPlaceFor(ItemStack is, int c, int r) {
        int w = is.getItem().getGridWidth();
        int h = is.getItem().getGridHeight();
        if(r < 0 || r + h > grid.length || h < 0 || c + w > grid[0].length) return false;
        for(int i = 0; i < h; i++) {
            for(int j = 0; j < w; j++) {
                if(r+i == grid.length || c+j == grid[0].length) return false;
                if(grid[r+i][c+j] != null && grid[r+i][c+j] != is) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isInside(int r, int c) {
        return r < grid.length && c < grid[0].length; // ak tak tu chyba 0
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
}
