package com.astetyne.expirium.client.items;

import java.util.HashMap;

public enum ItemRecipe {

    PLANKS(Item.PLANKS, new ItemStack[]{new ItemStack(Item.RAW_WOOD, 2)}, "Basic material for most of the tools."),
    WOODEN_MATTOCK(Item.WOODEN_MATTOCK, new ItemStack[]{new ItemStack(Item.LIMESTONE, 2), new ItemStack(Item.PLANKS, 10)}, "Better than nothing."),
    RHYOLITE_MATTOCK(Item.RHYOLITE_MATTOCK, new ItemStack[]{new ItemStack(Item.RHYOLITE, 10), new ItemStack(Item.PLANKS, 15)}, "Quite strong for soft materials."),
    IRON_MATTOCK(Item.IRON_MATTOCK, new ItemStack[]{new ItemStack(Item.IRON, 5), new ItemStack(Item.PLANKS, 15)}, "Durable, hard and fast!"),
    HUNTING_KNIFE(Item.HUNTING_KNIFE, new ItemStack[]{new ItemStack(Item.IRON, 10), new ItemStack(Item.PLANKS, 5)}, "Tired of vegan food?"),
    TORCH(Item.TORCH, new ItemStack[]{new ItemStack(Item.PLANKS, 3), new ItemStack(Item.CLAYSTONE, 1)}, "Light it up!"),
    WOODEN_WALL(Item.WOODEN_WALL, 2, new ItemStack[]{new ItemStack(Item.PLANKS, 2)}, "Good for basic house walls. High stability included."),
    SOFT_WOODEN_WALL(Item.SOFT_WOODEN_WALL, 2, new ItemStack[]{new ItemStack(Item.PLANKS, 2)}, "Same as wooden wall but softer to your body."),
    WOODEN_SUPPORT(Item.WOODEN_SUPPORT, 6, new ItemStack[]{new ItemStack(Item.RAW_WOOD, 3)}, "Cheap and useful for maintaining high stability."),
    LADDER(Item.LADDER, 2, new ItemStack[]{new ItemStack(Item.PLANKS, 4), new ItemStack(Item.RHYOLITE, 1)}, "Wood is maybe not the hardest material but do not be afraid."),
    LADDER_WALL(Item.LADDER_WALL, new ItemStack[]{new ItemStack(Item.PLANKS, 6)}, "Ladder and wooden wall in one piece."),
    CAMPFIRE(Item.CAMPFIRE, new ItemStack[]{new ItemStack(Item.RAW_WOOD, 10)}, "Make it if you are cold. It will warm your small house."),
    FURNACE(Item.FURNACE, new ItemStack[]{new ItemStack(Item.CLAYSTONE, 5), new ItemStack(Item.LIMESTONE, 20)}, "For cooking better meals."),
    RECYCLER(Item.RECYCLER, new ItemStack[]{new ItemStack(Item.IRON, 3), new ItemStack(Item.RHYOLITE, 5), new ItemStack(Item.LIMESTONE, 10)}, "Recycle stuff and make this planet greener."),
    CHEST(Item.CHEST, new ItemStack[]{new ItemStack(Item.PLANKS, 15), new ItemStack(Item.LIMESTONE, 4), new ItemStack(Item.IRON, 2)}, "Small storage for your items."),
    BOWL(Item.WOODEN_BOWL, new ItemStack[]{new ItemStack(Item.PLANKS, 3)}, "herobrine was added"),
    JAR(Item.JAR, new ItemStack[]{new ItemStack(Item.GLASS, 5)}, "Ideal for tasty jams."),
    TIME_WARPER(Item.TIME_WARPER, new ItemStack[]{new ItemStack(Item.CHROMIUM, 5), new ItemStack(Item.PLANKS, 20), new ItemStack(Item.GLASS, 10)}, "When die, you will appear near this but you have only one chance to place it!"),
    ;

    ItemStack product;
    ItemStack[] requiredItems;
    String description;

    ItemRecipe(Item product, ItemStack[] reqItems, String desc) {
        this.product = new ItemStack(product);
        this.requiredItems = reqItems;
        this.description = desc;
    }

    ItemRecipe(Item product, int amount, ItemStack[] reqItems, String desc) {
        this.product = new ItemStack(product, amount);
        this.requiredItems = reqItems;
        this.description = desc;
    }

    public int getId() {
        return id;
    }

    public ItemStack getProduct() {
        return product;
    }

    public ItemStack[] getRequiredItems() {
        return requiredItems;
    }

    public String getDescription() {
        return description;
    }

    public static ItemRecipe getRecipe(int id) {
        return map.get(id);
    }

    int id;
    private static final HashMap<Integer, ItemRecipe> map;
    static {
        map = new HashMap<>();
        int i = 0;
        for(ItemRecipe it : ItemRecipe.values()) {
            it.id = i;
            map.put(it.id, it);
            i++;
        }
    }
}
