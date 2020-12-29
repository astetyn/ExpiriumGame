package com.astetyne.expirium.main.items;

import com.astetyne.expirium.main.utils.IntVector2;

public class ItemStack {

    private final ItemType item;
    private int amount;
    private final IntVector2 gridPos;

    public ItemStack(ItemType item) {
        this(item, 1, new IntVector2(0,0));
    }

    public ItemStack(ItemType item, int amount) {
        this(item, amount, new IntVector2(0,0));
    }

    public ItemStack(ItemType item, int amount, IntVector2 pos) {
        this.item = item;
        this.amount = amount;
        gridPos = pos;
    }

    public ItemType getItem() {
        return item;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void increaseAmount(int increaseAmount) {
        amount += increaseAmount;
    }

    public void decreaseAmount(int decreaseAmount) {
        amount -= decreaseAmount;
    }

    public IntVector2 getGridPos() {
        return gridPos;
    }
}
