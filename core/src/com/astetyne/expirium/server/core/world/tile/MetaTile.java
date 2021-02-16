package com.astetyne.expirium.server.core.world.tile;

import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.client.tiles.Material;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.world.input.InteractType;
import com.astetyne.expirium.server.core.WorldSaveable;
import com.astetyne.expirium.server.core.entity.ExpiPlayer;
import com.astetyne.expirium.server.core.world.ExpiWorld;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.astetyne.expirium.server.core.world.inventory.Inventory;
import com.badlogic.gdx.math.Vector2;

public class MetaTile implements WorldSaveable {

    protected final ExpiWorld world;
    protected final ExpiTile owner;

    /** Only this constructor will be called. Do not change parameters in your superclass.*/
    public MetaTile(ExpiWorld world, ExpiTile owner) {
        this.world = world;
        this.owner = owner;
    }

    /** Returns true if meta should be kept. False if meta should be changed.*/
    public boolean onMaterialChange(Material to) {
        return false;
    }

    public void onInteract(ExpiPlayer p, InteractType type) {}

    public void dropItems() {
        if(owner.getMaterial().getDefaultDropItem() == null) return;
        dropItem(owner.getMaterial().getDefaultDropItem());
    }

    protected void dropInvItems(Inventory inventory) {
        for(ItemStack is : inventory.getItems()) {
            for(int i = 0; i < is.getAmount(); i++) {
                dropItem(is.getItem());
            }
        }
    }

    @Override
    public void writeData(WorldBuffer out) {}

    public void dropItem(Item item) {
        float off = (1 - EntityType.DROPPED_ITEM.getWidth())/2;
        Vector2 loc = new Vector2(owner.getX() + off, owner.getY() + off);
        world.spawnEntity(EntityType.DROPPED_ITEM, loc, item, Consts.ITEM_COOLDOWN_BREAK);
    }

    protected long scheduleAfter(Runnable runnable, long afterTicks) {
        return world.scheduleTaskAfter(() -> {
            if(owner.getMeta() != this) return;
            runnable.run();
        }, afterTicks);
    }

    protected long schedule(Runnable runnable, long tick) {
        return world.scheduleTask(() -> {
            if(owner.getMeta() != this) return;
            runnable.run();
        }, tick);
    }

}
