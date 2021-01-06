package com.astetyne.expirium.server.api.entities;

import com.astetyne.expirium.main.entity.EntityBodyFactory;
import com.astetyne.expirium.main.entity.EntityType;
import com.astetyne.expirium.main.items.Item;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.astetyne.expirium.server.backend.PacketOutputStream;
import com.badlogic.gdx.math.Vector2;

public class ExpiDroppedItem extends ExpiEntity {

    private int ticksToDespawn;
    private Item type;
    private int cooldown;

    public ExpiDroppedItem(Vector2 loc, Item type, int cooldown) {
        super(EntityType.DROPPED_ITEM, Consts.D_I_SIZE, Consts.D_I_SIZE);
        body = EntityBodyFactory.createDroppedEntityBody(loc);
        ticksToDespawn = Consts.SERVER_DEFAULT_TPS * 60;
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

    public Item getItem() {
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
        type = Item.getType(in.getInt());
    }

    @Override
    public void writeMeta(PacketOutputStream out) {
        out.putInt(type.getId());
    }
}
