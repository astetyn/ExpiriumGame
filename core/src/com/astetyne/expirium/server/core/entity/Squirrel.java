package com.astetyne.expirium.server.core.entity;

import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.astetyne.expirium.server.net.PacketOutputStream;
import com.badlogic.gdx.math.Vector2;

import java.io.DataInputStream;
import java.io.IOException;

public class Squirrel extends LivingEntity {

    private final static byte MAX_HEALTH = 30;

    private final Vector2 originLoc;
    private long nextAITick;

    public Squirrel(ExpiServer server, Vector2 loc) {
        super(server, EntityType.SQUIRREL, loc, MAX_HEALTH);
        originLoc = new Vector2(loc);
        nextAITick = server.getWorld().getTick();
    }

    public Squirrel(ExpiServer server, DataInputStream in) throws IOException {
        super(server, EntityType.SQUIRREL, MAX_HEALTH, in);
        originLoc = new Vector2(in.readFloat(), in.readFloat());
        nextAITick = server.getWorld().getTick();
    }

    @Override
    public void onTick() {
        super.onTick();
        if(server.getWorld().getTick() >= nextAITick) {
            Vector2 impulse;
            if(Math.random() > 0.5) {
                impulse = new Vector2((float) (Math.random()*100), (float) (Math.random()*100));
            }else {
                impulse = new Vector2((float) -(Math.random()*100), (float) (Math.random()*100));
            }
            float ox = originLoc.x - getLocation().x;
            float oy = originLoc.y - getLocation().y;
            if(underWater) {
                oy += 50;
            }
            impulse.add(ox, oy);
            body.applyLinearImpulse(impulse, getCenter(), true);
            nextAITick = server.getWorld().getTick() + Consts.SERVER_TPS * ((int)(Math.random() * 10) + 3);
        }
    }

    @Override
    public void die() {
        if(Math.random() > 0.6) {
            server.getWorld().spawnEntity(EntityType.DROPPED_ITEM, getCenter(), Item.SMALL_MEAT_RAW, Consts.ITEM_COOLDOWN_BREAK);
        }
        super.die();
    }

    @Override
    protected void recalcFallDamage() {
        if(lastFallVelocity < -20 && getVelocity().y - lastFallVelocity > 19) {
            injure((int) (lastFallVelocity*(-0.5)));
        }
    }

    @Override
    public void writeInitClientMeta(PacketOutputStream out) {}

    @Override
    public void writeData(WorldBuffer out) {
        super.writeData(out);
        out.writeFloat(originLoc.x);
        out.writeFloat(originLoc.y);
    }
}
