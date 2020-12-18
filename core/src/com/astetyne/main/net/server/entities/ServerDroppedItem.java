package com.astetyne.main.net.server.entities;

import com.astetyne.main.items.ItemType;
import com.astetyne.main.utils.Constants;
import com.badlogic.gdx.math.Vector2;

public class ServerDroppedItem extends ServerEntity {

    private int ticksToDespawn;
    private final ItemType type;
    private int cooldown;

    public ServerDroppedItem(Vector2 location, ItemType type, int cooldown) {
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

    public ItemType getType() {
        return type;
    }

    public void reduceCooldown() {
        cooldown--;
    }

    public int getCooldown() {
        return cooldown;
    }
}
