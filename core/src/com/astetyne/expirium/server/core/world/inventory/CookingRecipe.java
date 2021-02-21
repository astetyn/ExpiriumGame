package com.astetyne.expirium.server.core.world.inventory;

import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.client.utils.Consts;

public enum CookingRecipe {

    COOKED_APPLE(Consts.SERVER_TPS*10, new ItemStack[]{new ItemStack(Item.APPLE)}, new ItemStack(Item.COOKED_APPLE)),
    FRUIT_JAM(Consts.SERVER_TPS*20, new ItemStack[]{new ItemStack(Item.RASPBERRY), new ItemStack(Item.APPLE), new ItemStack(Item.WOODEN_BOWL)}, new ItemStack(Item.FRUIT_JAM)),
    GLASS(Consts.SERVER_TPS*5, new ItemStack[]{new ItemStack(Item.SAND, 2)}, new ItemStack(Item.GLASS)),
    BLUEBERRY_JAM(Consts.SERVER_TPS*20, new ItemStack[]{new ItemStack(Item.BLUEBERRY, 2), new ItemStack(Item.JAR)}, new ItemStack(Item.JAR_BLUEBERRY_JAM)),
    RASPBERRY_JAM(Consts.SERVER_TPS*20, new ItemStack[]{new ItemStack(Item.RASPBERRY, 2), new ItemStack(Item.JAR)}, new ItemStack(Item.JAR_RASPBERRY_JAM)),
    IRON(Consts.SERVER_TPS*30, new ItemStack[]{new ItemStack(Item.MAGNETITE)}, new ItemStack(Item.IRON)),
    CHROMIUM(Consts.SERVER_TPS*30, new ItemStack[]{new ItemStack(Item.CHROMITE)}, new ItemStack(Item.CHROMIUM)),
    SMALL_MEAT(Consts.SERVER_TPS*25, new ItemStack[]{new ItemStack(Item.SMALL_MEAT_RAW)}, new ItemStack(Item.SMALL_MEAT_COOKED)),
    ;

    private final int ticks;
    private final ItemStack[] requiredItems;
    private final ItemStack product;

    CookingRecipe(int ticks, ItemStack[] requiredItems, ItemStack product) {
        this.ticks = ticks;
        this.requiredItems = requiredItems;
        this.product = product;
    }

    public int getTicks() {
        return ticks;
    }

    public ItemStack[] getRequiredItems() {
        return requiredItems;
    }

    public ItemStack getProduct() {
        return product;
    }
}
