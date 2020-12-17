package com.astetyne.main.net.server.entities;

import com.astetyne.main.Constants;
import com.astetyne.main.items.ItemType;
import com.badlogic.gdx.math.Vector2;

public class ServerDroppedItem extends ServerEntity {

    private int ticksToDespawn;
    private final ItemType type;

    public ServerDroppedItem(Vector2 location, ItemType type) {
        super(location, Constants.D_I_SIZE, Constants.D_I_SIZE);
        ticksToDespawn = Constants.SERVER_DEFAULT_TPS * 60;
        this.type = type;
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
}
