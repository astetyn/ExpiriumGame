package com.astetyne.expirium.server.api.entity;

import com.astetyne.expirium.main.entity.EntityBodyFactory;
import com.astetyne.expirium.main.entity.EntityType;
import com.astetyne.expirium.main.items.Item;
import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.event.TickListener;
import com.astetyne.expirium.server.backend.PacketOutputStream;
import com.badlogic.gdx.math.Vector2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ExpiDroppedItem extends ExpiEntity implements TickListener {

    private float livingTime;
    private Item item;
    private final float pickCooldown, despawnTime;

    public ExpiDroppedItem(Vector2 loc, Item item, float pickCooldown) {
        super(EntityType.DROPPED_ITEM, Consts.D_I_SIZE, Consts.D_I_SIZE);
        body = EntityBodyFactory.createDroppedEntityBody(loc);
        this.item = item;
        this.pickCooldown = pickCooldown;
        livingTime = 0;
        despawnTime = Consts.ITEM_DESPAWN_TIME;
        body.setAngularVelocity(((float)Math.random()-0.5f)*10);
        GameServer.get().getEventManager().getTickListeners().add(this);
    }

    public ExpiDroppedItem(DataInputStream in) throws IOException {
        super(EntityType.DROPPED_ITEM, Consts.D_I_SIZE, Consts.D_I_SIZE);
        body = EntityBodyFactory.createDroppedEntityBody(new Vector2(in.readFloat(), in.readFloat()));
        item = Item.getType(in.readInt());
        pickCooldown = 5;
        livingTime = 0;
        despawnTime = Consts.ITEM_DESPAWN_TIME;
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
    public void writeMeta(PacketOutputStream out) {
        out.putInt(item.getId());
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeFloat(getLocation().x);
        out.writeFloat(getLocation().y);
        out.writeInt(item.getId());
    }
}
