package com.astetyne.expirium.client.items;

public class ItemStack {

    protected Item item;
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

    public void setItem(Item item) {
        this.item = item;
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

    public float getWeight() {
        return item.getWeight() * amount;
    }

}
