package com.astetyne.expirium.server.core.world.inventory;

import com.astetyne.expirium.client.items.GridItemStack;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.client.utils.IntVector2;
import com.astetyne.expirium.server.core.WorldSaveable;
import com.astetyne.expirium.server.core.entity.player.ExpiPlayer;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Notes: 0,0 is in the bottom left corner, x and y are as normal
 */
public class Inventory implements WorldSaveable {

    protected final List<GridItemStack> items;
    protected final GridItemStack[][] grid;
    protected float totalWeight;
    protected final float maxWeight;
    protected String label;
    protected final int rows, columns;
    private final HashSet<ExpiPlayer> viewersSinceUpdate;

    public Inventory(int rows, int columns, float maxWeight) {
        this.rows = rows;
        this.columns = columns;
        this.maxWeight = maxWeight;
        items = new ArrayList<>();
        grid = new GridItemStack[columns][rows];
        totalWeight = 0;
        label = "";
        viewersSinceUpdate = new HashSet<>();
    }

    public Inventory(int rows, int columns, float maxWeight, DataInputStream in) throws IOException {
        this(rows, columns, maxWeight);
        int itemsSize = in.readInt();
        for(int i = 0; i < itemsSize; i++) {
            GridItemStack is = new GridItemStack(in);
            items.add(is);
            insertToGrid(is);
        }
        totalWeight = in.readFloat();
    }

    public boolean canAppend(ItemStack is) {
        return canAppend(is.getItem(), is.getAmount());
    }

    public boolean canAppend(Item item, int amount) {
        if(totalWeight + item.getWeight() * amount > maxWeight) return false;
        if(item.isMergeable()) {
            for(ItemStack is : items) {
                if(is.getItem() == item) return true;
            }
        }
        for(int x = 0; x < columns; x++) {
            for(int y = 0; y < rows; y++) {
                if(canInsert(item, x, y)) return true;
            }
        }
        return false;
    }

    public boolean canInsert(ItemStack is, IntVector2 pos) {
        if(totalWeight + is.getWeight() > maxWeight) return false;
        return canInsert(is.getItem(), pos.x, pos.y);
    }

    protected boolean canInsert(Item item, int x, int y) {
        int gw = item.getGridWidth();
        int gh = item.getGridHeight();
        if(gw + x > columns || gh + y > rows) return false;
        for(int x2 = 0; x2 < gw; x2++) {
            for(int y2 = 0; y2 < gh; y2++) {
                if(grid[x + x2][y + y2] != null) return false;
            }
        }
        return true;
    }

    public void append(ItemStack is) {
        append(is.getItem(), is.getAmount());
    }

    public void append(Item item, int amount) {
        if(item.isMergeable()) {
            for(ItemStack is : items) {
                if(is.getItem() == item) {
                    is.increaseAmount(amount);
                    increaseWeight(item.getWeight() * amount);
                    refresh();
                    return;
                }
            }
        }
        for(int y = rows - 1; y >= 0; y--) {
            for(int x = 0; x < columns; x++) {
                if(canInsert(item, x, y)) {
                    GridItemStack gis = new GridItemStack(item, amount, new IntVector2(x, y));
                    items.add(gis);
                    insertToGrid(gis);
                    increaseWeight(gis.getWeight());
                    refresh();
                    return;
                }
            }
        }
    }

    public void insert(ItemStack is, IntVector2 pos) {
        if(!canInsert(is, pos)) return;
        GridItemStack gis = new GridItemStack(is, pos);
        insertToGrid(gis);
        items.add(gis);
        increaseWeight(gis.getWeight());
        refresh();
    }

    public void remove(ItemStack is) {
        remove(is.getItem(), is.getAmount());
    }

    public void remove(Item item, int amount) {
        int removedAmount = 0;
        Iterator<GridItemStack> it = items.iterator();
        while(it.hasNext()) {
            GridItemStack gis = it.next();
            if(gis.getItem() == item) {
                int toBeRemoved = Math.min(gis.getAmount(), amount - removedAmount);
                removedAmount += toBeRemoved;
                gis.decreaseAmount(toBeRemoved);
                if(gis.getAmount() == 0) {
                    it.remove();
                    cleanGridFrom(gis);
                }
                if(removedAmount == amount) {
                    decreaseWeight(item.getWeight() * removedAmount);
                    refresh();
                    return;
                }
            }
        }
        // remove was not completely successful
        decreaseWeight(item.getWeight() * removedAmount);
        refresh();
    }

    protected void remove(IntVector2 pos) {
        GridItemStack gis = grid[pos.x][pos.y];
        cleanGridFrom(gis);
        decreaseWeight(gis.getWeight());
        items.remove(gis);
        refresh();
    }

    public void move(IntVector2 pos1, IntVector2 pos2) {
        GridItemStack gis1 = grid[pos1.x][pos1.y];
        GridItemStack gis2 = grid[pos2.x][pos2.y];
        if(gis1 == null) return;
        if(gis1.getItem().isMergeable() && gis2 != null && gis2.getItem() == gis1.getItem() && gis1 != gis2) {
            gis2.increaseAmount(gis1.getAmount());
            items.remove(gis1);
            cleanGridFrom(gis1);
            refresh();
            return;
        }
        cleanGridFrom(gis1);
        if(!canInsert(gis1.getItem(), pos2.x, pos2.y)) {
            insertToGrid(gis1);
            return;
        }
        gis1.getGridPos().set(pos2);
        insertToGrid(gis1);
        refresh();
    }

    public void refresh() {
        viewersSinceUpdate.clear();
    }

    public boolean contains(ItemStack[] itemStacks) {
        outer:
        for(ItemStack is : itemStacks) {
            int remainingToFind = is.getAmount();
            for(GridItemStack gis : items) {
                if(gis.getItem() == is.getItem()) {
                    remainingToFind -= gis.getAmount();
                    if(remainingToFind <= 0) {
                        continue outer;
                    }
                }
            }
            return false;
        }
        return true;
    }

    public boolean contains(Item item) {
        for(GridItemStack is : items) {
            if(is.getItem() == item) return true;
        }
        return false;
    }

    public boolean contains(Item item, int amount) {
        for(GridItemStack is : items) {
            if(is.getItem() == item && is.getAmount() >= amount) return true;
        }
        return false;
    }

    public void clear() {
        for(GridItemStack is : items) {
            cleanGridFrom(is);
        }
        totalWeight = 0;
        items.clear();
        refresh();
    }

    private void insertToGrid(GridItemStack gis) {
        for(int x = 0; x < gis.getItem().getGridWidth(); x++) {
            for(int y = 0; y < gis.getItem().getGridHeight(); y++) {
                grid[gis.getGridPos().x+x][gis.getGridPos().y+y] = gis;
            }
        }
    }

    private void cleanGridFrom(GridItemStack gis) {
        for(int x = 0; x < gis.getItem().getGridWidth(); x++) {
            for(int y = 0; y < gis.getItem().getGridHeight(); y++) {
                grid[gis.getGridPos().x+x][gis.getGridPos().y+y] = null;
            }
        }
    }

    public GridItemStack getItemOn(IntVector2 pos) {
        return grid[pos.x][pos.y];
    }

    public String getLabel() {
        return label;
    }

    public List<GridItemStack> getItems() {
        return items;
    }

    public float getTotalWeight() {
        return totalWeight;
    }

    public float getMaxWeight() {
        return maxWeight;
    }

    public GridItemStack[][] getGrid() {
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
        totalWeight = Math.round(totalWeight * 100) / 100f;
    }

    public void decreaseWeight(float f) {
        totalWeight -= f;
        totalWeight = Math.round(totalWeight * 100) / 100f;
    }

    public boolean needsUpdate(ExpiPlayer whoAsks) {
        return !viewersSinceUpdate.contains(whoAsks);
    }

    public void willNeedUpdate() {
        viewersSinceUpdate.clear();
    }

    public void wasUpdated(ExpiPlayer viewer) {
        viewersSinceUpdate.add(viewer);
    }

    @Override
    public void writeData(WorldBuffer out) {
        out.writeInt(items.size());
        for(GridItemStack is : items) {
            is.writeData(out);
        }
        out.writeFloat(totalWeight);
    }

    /*private void printGrid() {
        for(int x = 0; x < columns; x++) {
            for(int y = 0; y < rows; y++) {
                ItemStack is = grid[x][y];
                if(is == null) {
                    System.out.print("[]");
                }else {
                    System.out.print("[" + is.getItem().name().charAt(0) + "]");
                }
            }
            System.out.println();
        }
    }*/
}
