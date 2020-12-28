package com.astetyne.expirium.server.api;

import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.items.ItemType;
import com.astetyne.expirium.main.utils.IntVector2;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.entities.ExpiPlayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExpiInventory {

    private final List<ItemStack> items;
    private final int id;
    private final ItemStack[][] grid;

    public ExpiInventory(int columns, int rows) {
        items = new ArrayList<>();
        int randomID;
        do {
            randomID = (int)(Math.random()*Integer.MAX_VALUE);
        } while(GameServer.get().getInventoriesID().containsKey(randomID));
        GameServer.get().getInventoriesID().put(randomID, this);
        id = randomID;
        grid = new ItemStack[rows][columns];
    }

    public boolean canBeAdded(ItemType item) {
        //todo: check space + weight
        return true;
    }

    public void addItem(ItemStack addIS) {
        for(ItemStack is : items) {
            if(is.getItem() == addIS.getItem()) {
                is.increaseAmount(addIS.getAmount());
                return;
            }
        }
        int w = addIS.getItem().getGridWidth();
        int h = addIS.getItem().getGridHeight();
        for(int i = h-1; i < grid.length; i--) {
            columer:
            for(int j = 0; j <= grid[0].length - w; j++) {
                for(int ii = i; ii < i + h; ii++) {
                    for(int jj = j; jj < j + w; jj++) {
                        if(grid[ii][jj] != null) {
                            continue columer;
                        }
                    }
                }
                addIS.getGridPos().set(i, j);
                for(int ii = i; ii < i + h; ii++) {
                    for(int jj = j; jj < j + w; jj++) {
                        grid[ii][jj] = addIS;
                    }
                }
                items.add(addIS);
                return;
            }
        }
    }

    public void onMoveReq(ExpiPlayer p, IntVector2 pos1, IntVector2 pos2) {
        //todo: vygenerovat moveAck
    }

    public boolean contain(ItemType item) {
        for(ItemStack is : items) {
            if(is.getItem() == item) {
                return true;
            }
        }
        return false;
    }

    public void removeItem(ItemStack remIS) {
        Iterator<ItemStack> it = items.iterator();
        while(it.hasNext()) {
            ItemStack is = it.next();
            if(is.getItem() == remIS.getItem()) {
                is.decreaseAmount(remIS.getAmount());
                if(is.getAmount() <= 0) {
                    it.remove();
                    int i = is.getGridPos().x;
                    int j = is.getGridPos().y;
                    int w = is.getItem().getGridWidth();
                    int h = is.getItem().getGridHeight();
                    for(int ii = i; ii < i + h; ii++) {
                        for(int jj = j; jj < j + w; jj++) {
                            grid[ii][jj] = null;
                        }
                    }
                    break;
                }
            }
        }
    }

    public int getId() {
        return id;
    }

    public List<ItemStack> getItems() {
        return items;
    }
}
