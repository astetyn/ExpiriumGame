package com.astetyne.expirium.server.api.entity;

import com.astetyne.expirium.main.entity.EntityBodyFactory;
import com.astetyne.expirium.main.entity.EntityType;
import com.astetyne.expirium.main.items.Item;
import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.event.TickListener;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.astetyne.expirium.server.backend.PacketOutputStream;
import com.badlogic.gdx.math.Vector2;

public class ExpiDroppedItem extends ExpiEntity implements TickListener {

    private float livingTime;
    private Item item;
    private final float pickCooldown, despawnTime;

    public ExpiDroppedItem(Vector2 loc, Item item, float pickCooldown) {
        super(EntityType.DROPPED_ITEM, Consts.D_I_SIZE, Consts.D_I_SIZE);
        body = EntityBodyFactory.createDroppedEntityBody(loc);
        livingTime = 0;
        despawnTime = 10; // in seconds
        this.item = item;
        this.pickCooldown = pickCooldown;
        body.setAngularVelocity(((float)Math.random()-0.5f)*10);
        GameServer.get().getEventManager().getTickListeners().add(this);
    }

    @Override
    public void onTick() {

        livingTime += 1f / Consts.SERVER_DEFAULT_TPS;

        if(livingTime >= despawnTime) {
            for(ExpiPlayer pp : GameServer.get().getPlayers()) {
                pp.getNetManager().putEntityDespawnPacket(this);
            }
            destroy();
        }

        if(livingTime < pickCooldown) {
            return;
        }
        for(ExpiPlayer p : GameServer.get().getPlayers()) {
            Vector2 dif = p.getCenter().sub(getCenter());
            if(dif.len() < Consts.D_I_PICK_DIST && p.getInv().canBeAdded(item, 1)) {
                p.getInv().addItem(new ItemStack(item), true);
                p.getNetManager().putInvFeedPacket();
                for(ExpiPlayer pp : GameServer.get().getPlayers()) {
                    pp.getNetManager().putEntityDespawnPacket(this);
                }
                destroy();
            }
        }
    }

    public Item getItem() {
        return item;
    }

    public void destroy() {
        GameServer.get().getEventManager().getTickListeners().remove(this);
        super.destroy();
    }

    @Override
    public void readMeta(PacketInputStream in) {
        item = Item.getType(in.getInt());
    }

    @Override
    public void writeMeta(PacketOutputStream out) {
        out.putInt(item.getId());
    }

}
