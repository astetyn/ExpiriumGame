package com.astetyne.expirium.main.items;

import com.astetyne.expirium.main.utils.IntVector2;

public class ItemStack {

    private final Item item;
    private int amount;
    private final IntVector2 gridPos;

    public ItemStack(ItemStack is) {
        this.item = is.getItem();
        this.amount = is.getAmount();
        this.gridPos = new IntVector2(is.getGridPos());
    }

    public ItemStack(Item item) {
        this(item, 1, new IntVector2(0,0));
    }

    public ItemStack(Item item, int amount) {
        this(item, amount, new IntVector2(0,0));
    }

    public ItemStack(Item item, int amount, IntVector2 pos) {
        this.item = item;
        this.amount = amount;
        gridPos = pos;
    }

    public Item getItem() {
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

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof ItemStack)) return false;
        ItemStack is = (ItemStack) o;
        return item == is.getItem() && amount == is.getAmount();
    }
}
