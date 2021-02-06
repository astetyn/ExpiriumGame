package com.astetyne.expirium.server.core.world.tile;

import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.tiles.Solidity;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.Saveable;
import com.astetyne.expirium.server.core.event.TileChangeEvent;
import com.badlogic.gdx.math.Vector2;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class MetaTile implements Saveable {

    protected final ExpiServer server;
    protected final ExpiTile owner;

    /** Only this constructor will be called. Do not change parameters in your superclass.*/
    public MetaTile(ExpiServer server, ExpiTile owner) {
        this.server = server;
        this.owner = owner;
    }

    public void onTick() {}

    public void onTileChange(TileChangeEvent e) {}

    public void dropItems() {}

    public abstract Solidity getSolidity();

    public abstract TileFix getFix();

    public abstract int getMaxStability();

    public abstract float getBreakTime();

    public boolean isTransparent() {
        return getFix() == TileFix.SOFT;
    }

    public boolean isWall() {
        return false;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {}

    public void dropItem(Item item) {
        float off = (1 - EntityType.DROPPED_ITEM.getWidth())/2;
        Vector2 loc = new Vector2(owner.getX() + off, owner.getY() + off);
        server.getWorld().spawnDroppedItem(item, loc, Consts.ITEM_COOLDOWN_BREAK);
    }

}
