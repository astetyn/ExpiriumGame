package com.astetyne.expirium.server.api.world.inventory;

import com.astetyne.expirium.main.items.Item;
import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.main.utils.IntVector2;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.entities.ExpiDroppedItem;
import com.astetyne.expirium.server.api.entities.ExpiPlayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExpiInventory {

    protected final List<ItemStack> items;
    protected final int id;
    protected final ItemStack[][] grid;
    protected float totalWeight;
    protected final float maxWeight;

    public ExpiInventory(int columns, int rows, float maxWeight) {
        items = new ArrayList<>();
        int randomID;
        do {
            randomID = (int)(Math.random()*Integer.MAX_VALUE);
        } while(GameServer.get().getInventoriesID().containsKey(randomID));
        GameServer.get().getInventoriesID().put(randomID, this);
        id = randomID;
        grid = new ItemStack[rows][columns];
        totalWeight = 0;
        this.maxWeight = maxWeight;
    }

    public boolean canBeAdded(Item item, int amount) {
        if(totalWeight + item.getWeight() * amount > maxWeight) return false;



        return true;
    }

    public void addItem(ItemStack addIS) {
        ItemStack copyIS = new ItemStack(addIS);

        for(ItemStack is : items) {
            if(is.getItem() == copyIS.getItem()) {
                is.increaseAmount(copyIS.getAmount());
                totalWeight += copyIS.getItem().getWeight() * copyIS.getAmount();
                return;
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
                return;
            }
        }
    }

    public void onMoveReq(ExpiPlayer p, IntVector2 pos1, IntVector2 pos2) {
        ItemStack is = grid[pos1.y][pos1.x];
        if(is == null) return;
        if(pos2.x == -1) {
            removeItem(is);
            for(ExpiPlayer p2 : GameServer.get().getPlayers()) {
                //todo: vytvorit spravnu lokaciu itemu, podla otocenia hraca? podla okolitych blokov?
                ExpiDroppedItem edi = new ExpiDroppedItem(p.getCenter(), is.getItem(), Consts.SERVER_DEFAULT_TPS);
                p2.getGateway().getManager().putEntitySpawnPacket(edi);
            }
            p.getGateway().getManager().putMainInvFeedPacket(this);
            return;
        }
        if(!isPlaceFor(is, pos2.x, pos2.y)) return;
        cleanGridFrom(is);
        is.getGridPos().set(pos2);
        insertToGrid(is);
        p.getGateway().getManager().putMainInvFeedPacket(this);
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

    private void insertToGrid(ItemStack is) {
        for(int i = 0; i < is.getItem().getGridHeight(); i++) {
            for(int j = 0; j < is.getItem().getGridWidth(); j++) {
                grid[is.getGridPos().y+i][is.getGridPos().x+j] = is;
            }
        }
    }

    private void cleanGridFrom(ItemStack is) {
        for(int i = 0; i < is.getItem().getGridHeight(); i++) {
            for(int j = 0; j < is.getItem().getGridWidth(); j++) {
                grid[is.getGridPos().y+i][is.getGridPos().x+j] = null;
            }
        }
    }

    private boolean isPlaceFor(ItemStack is, int c, int r) {
        for(int i = 0; i < is.getItem().getGridHeight(); i++) {
            for(int j = 0; j < is.getItem().getGridWidth(); j++) {
                if(r+i == grid.length || c+j == grid[0].length) return false;
                if(grid[r+i][c+j] != null && grid[r+i][c+j] != is) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getId() {
        return id;
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
}
