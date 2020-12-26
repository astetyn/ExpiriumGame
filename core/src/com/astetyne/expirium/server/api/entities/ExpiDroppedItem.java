package com.astetyne.expirium.server.api.entities;

import com.astetyne.expirium.main.entity.EntityType;
import com.astetyne.expirium.main.items.ItemType;
import com.astetyne.expirium.main.utils.Constants;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.astetyne.expirium.server.backend.PacketOutputStream;
import com.badlogic.gdx.math.Vector2;

public class ExpiDroppedItem extends ExpiEntity {

    private int ticksToDespawn;
    private ItemType type;
    private int cooldown;

    public ExpiDroppedItem(Vector2 loc, ItemType type, int cooldown) {
        super(EntityType.DROPPED_ITEM, loc, Constants.D_I_SIZE, Constants.D_I_SIZE);
        ticksToDespawn = Constants.SERVER_DEFAULT_TPS * 60;
        this.type = type;
        this.cooldown = cooldown;
        body.setAngularVelocity(((float)Math.random()-0.5f)*10);
        GameServer.get().getDroppedItems().add(this);
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

    public void destroy() {
        super.destroy();
        GameServer.get().getDroppedItems().remove(this);
    }

    public void destroySafe() {
        super.destroy();
    }

    @Override
    public void readMeta(PacketInputStream in) {
        type = ItemType.getType(in.getInt());
    }

    @Override
    public void writeMeta(PacketOutputStream out) {
        out.putInt(type.getId());
    }
}
