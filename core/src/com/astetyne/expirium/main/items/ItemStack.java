package com.astetyne.expirium.main.items;

public class ItemStack {

    protected final Item item;
    protected int amount;

    public ItemStack(ItemStack is) {
        this.item = is.getItem();
        this.amount = is.getAmount();
    }

    public ItemStack(Item item) {
        this(item, 1);
    }

    public ItemStack(Item item, int amount) {
        this.item = item;
        this.amount = amount;
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

}
