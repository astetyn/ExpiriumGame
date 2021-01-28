package com.astetyne.expirium.server.api.entity;

import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.api.event.TickListener;
import com.astetyne.expirium.server.net.PacketOutputStream;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ExpiDroppedItem extends ExpiEntity implements TickListener {

    private float livingTime;
    private final Item item;
    private final float pickCooldown, despawnTime;

    public ExpiDroppedItem(ExpiServer server, Vector2 loc, Item item, float pickCooldown) {
        super(server, EntityType.DROPPED_ITEM, loc);
        this.item = item;
        this.pickCooldown = pickCooldown;
        livingTime = 0;
        despawnTime = Consts.ITEM_DESPAWN_TIME;
        body.setAngularVelocity(((float)Math.random()-0.5f)*10);
        createBodyFixtures();
        server.getEventManager().getTickListeners().add(this);
    }

    public ExpiDroppedItem(ExpiServer server, DataInputStream in) throws IOException {
        super(server, EntityType.DROPPED_ITEM, in);
        item = Item.getType(in.readInt());
        pickCooldown = 5;
        livingTime = 0;
        despawnTime = Consts.ITEM_DESPAWN_TIME;
        createBodyFixtures();
        server.getEventManager().getTickListeners().add(this);
    }

    public void createBodyFixtures() {

        PolygonShape polyShape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();

        polyShape.setAsBox(0.25f, 0.25f);
        fixtureDef.density = 30f;
        fixtureDef.restitution = 0.2f;
        fixtureDef.friction = 0.2f;
        fixtureDef.shape = polyShape;
        fixtureDef.filter.categoryBits = Consts.DEFAULT_BIT;

        body.createFixture(fixtureDef);
        polyShape.dispose();

    }

    @Override
    public void onTick() {

        livingTime += 1f / Consts.SERVER_DEFAULT_TPS;

        if(livingTime >= despawnTime) {
            for(ExpiPlayer pp : server.getPlayers()) {
                pp.getNetManager().putEntityDespawnPacket(this);
            }
            destroy();
        }

        if(livingTime < pickCooldown) {
            return;
        }
        for(ExpiPlayer p : server.getPlayers()) {
            Vector2 dif = p.getCenter().sub(getCenter());
            if(dif.len() < Consts.D_I_PICK_DIST && p.getInv().canBeAdded(item, 1)) {
                p.getInv().addItem(new ItemStack(item), true);
                p.getNetManager().putInvFeedPacket();
                for(ExpiPlayer pp : server.getPlayers()) {
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
        server.getEventManager().getTickListeners().remove(this);
        super.destroy();
    }

    @Override
    public void writeMeta(PacketOutputStream out) {
        out.putInt(item.getId());
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        super.writeData(out);
        out.writeInt(item.getId());
    }
}
