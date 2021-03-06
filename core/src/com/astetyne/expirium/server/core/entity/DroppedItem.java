package com.astetyne.expirium.server.core.entity;

import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.ExpiColor;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.entity.player.Player;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.astetyne.expirium.server.net.PacketOutputStream;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import java.io.DataInputStream;
import java.io.IOException;

public class DroppedItem extends Entity {

    private final Item item;
    private final long pickTick;
    private final int ticksCooldown;

    public DroppedItem(ExpiServer server, Vector2 loc, Item item, Integer ticksCooldown) {
        super(server, EntityType.DROPPED_ITEM, loc);
        this.item = item;
        pickTick = server.getWorld().getTick();
        this.ticksCooldown = ticksCooldown;
        body.setAngularVelocity(((float)Math.random()-0.5f)*10);
        server.getWorld().scheduleTaskAfter(this::checkPick, 8); //every 8 ticks
        server.getWorld().scheduleTaskAfter(this::destroy, Consts.SERVER_TPS*Consts.ITEM_DESPAWN_TIME);
    }

    public DroppedItem(ExpiServer server, DataInputStream in) throws IOException {
        super(server, EntityType.DROPPED_ITEM, in);
        item = Item.get(in.readInt());
        pickTick = server.getWorld().getTick();
        ticksCooldown = Consts.SERVER_TPS * 3;
        body.setAngularVelocity(((float)Math.random()-0.5f)*10);
        server.getWorld().scheduleTaskAfter(this::checkPick, 8); //every 8 ticks
        server.getWorld().scheduleTaskAfter(this::destroy, Consts.SERVER_TPS*Consts.ITEM_DESPAWN_TIME);
    }

    public void createBodyFixtures() {

        PolygonShape polyShape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();

        polyShape.setAsBox(type.getWidth()/2, type.getHeight()/2, new Vector2(type.getWidth()/2, type.getHeight()/2), 0);
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
            server.getWorld().scheduleTaskAfter(this::checkPick, 8);
            return;
        }

        for(Player p : server.getPlayers()) {
            Vector2 dif = p.getCenter().sub(getCenter());
            if(dif.len() < Consts.D_I_PICK_DIST && p.getInv().canAppend(item, 1)) {
                p.getInv().append(item, 1);
                String text = "+ "+item.getLabel();
                p.getNetManager().putPlayTextAnim(getCenter().add(0, p.getHeight()/2), text, ExpiColor.ORANGE);
                destroy();
                return;
            }
        }
        server.getWorld().scheduleTaskAfter(this::checkPick, 8);
    }

    public Item getItem() {
        return item;
    }

    @Override
    public void writeInitClientMeta(PacketOutputStream out) {
        out.putInt(item.ordinal());
    }

    @Override
    public void writeData(WorldBuffer out) {
        super.writeData(out);
        out.writeInt(item.ordinal());
    }
}
