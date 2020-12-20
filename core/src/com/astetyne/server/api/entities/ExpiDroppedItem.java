package com.astetyne.server.api.entities;

import com.astetyne.main.entity.EntityType;
import com.astetyne.main.items.ItemType;
import com.astetyne.main.utils.Constants;
import com.badlogic.gdx.math.Vector2;

public class ExpiDroppedItem extends ExpiEntity {

    private int ticksToDespawn;
    private final ItemType type;
    private int cooldown;

    public ExpiDroppedItem(Vector2 location, ItemType type, int cooldown) {
        super(location, Constants.D_I_SIZE, Constants.D_I_SIZE);
        ticksToDespawn = Constants.SERVER_DEFAULT_TPS * 60;
        this.type = type;
        this.cooldown = cooldown;
    }

    public int getTicksToDespawn() {
        return ticksToDespawn;
    }

    public void setTicksToDespawn(int ticksToDespawn) {
        this.ticksToDespawn = ticksToDespawn;
    }

    public ItemType getItemType() {
        return type;
    }

    public void reduceCooldown() {
        cooldown--;
    }

    public int getCooldown() {
        return cooldown;
    }


    @Override
    public EntityType getType() {
        return EntityType.DROPPED_ITEM;
    }
}
