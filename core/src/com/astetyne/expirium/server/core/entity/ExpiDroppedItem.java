package com.astetyne.expirium.server.core.entity;

import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.astetyne.expirium.server.net.PacketOutputStream;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import java.io.DataInputStream;
import java.io.IOException;

public class ExpiDroppedItem extends ExpiEntity {

    private final Item item;
    private final long pickTick;
    private final int ticksCooldown;

    public ExpiDroppedItem(ExpiServer server, Vector2 loc, Item item, int ticksCooldown) {
        super(server, EntityType.DROPPED_ITEM, loc);
        this.item = item;
        pickTick = server.getWorld().getTick();
        this.ticksCooldown = ticksCooldown;
        body.setAngularVelocity(((float)Math.random()-0.5f)*10);
        postInit();
        server.getWorld().scheduleTask(this::checkPick, 8); //every 8 ticks
        server.getWorld().scheduleTask(this::destroy, Consts.SERVER_TPS*Consts.ITEM_DESPAWN_TIME);
    }

    public ExpiDroppedItem(ExpiServer server, DataInputStream in) throws IOException {
        super(server, EntityType.DROPPED_ITEM, in);
        item = Item.getType(in.readInt());
        pickTick = server.getWorld().getTick();
        ticksCooldown = Consts.SERVER_TPS * 3;
        postInit();
        server.getWorld().scheduleTask(this::checkPick, 8); //every 8 ticks
        server.getWorld().scheduleTask(this::destroy, Consts.SERVER_TPS*Consts.ITEM_DESPAWN_TIME);
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

    public void checkPick() {
        if(destroyed) return;
        if(pickTick + ticksCooldown >= server.getWorld().getTick()) {
            server.getWorld().scheduleTask(this::checkPick, 8);
            return;
        }

        for(ExpiPlayer p : server.getPlayers()) {
            Vector2 dif = p.getCenter().sub(getCenter());
            if(dif.len() < Consts.D_I_PICK_DIST && p.getInv().canAppend(item, 1)) {
                p.getInv().append(item, 1);
                destroy();
                return;
            }
        }
        server.getWorld().scheduleTask(this::checkPick, 8);
    }

    public Item getItem() {
        return item;
    }

    @Override
    public void writeMeta(PacketOutputStream out) {
        out.putInt(item.getId());
    }

    @Override
    public void writeData(WorldBuffer out) {
        super.writeData(out);
        out.writeInt(item.getId());
    }
}
