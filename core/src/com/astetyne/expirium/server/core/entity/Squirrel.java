package com.astetyne.expirium.server.core.entity;

import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.net.PacketOutputStream;
import com.badlogic.gdx.math.Vector2;

import java.io.DataInputStream;
import java.io.IOException;

public class Squirrel extends LivingEntity {

    public Squirrel(ExpiServer server, Vector2 loc) {
        super(server, EntityType.SQUIRREL, loc, 30);
    }

    public Squirrel(ExpiServer server, DataInputStream in) throws IOException {
        super(server, EntityType.SQUIRREL, 30, in);
    }

    @Override
    public void onTick() {
        super.onTick();
        if(server.getWorld().getTick() % 160 == (int) (Math.random()*160)) {
            if(Math.random() > 0.5) {
                body.applyLinearImpulse((float) (Math.random()*150), (float) (Math.random()*150), getCenter().x, getCenter().y, true);
            }else {
                body.applyLinearImpulse((float) -(Math.random()*150), (float) (Math.random()*150), getCenter().x, getCenter().y, true);
            }
        }
    }

    @Override
    public void die() {
        super.die();
        if(Math.random() > 0.5) {
            server.getWorld().spawnEntity(EntityType.DROPPED_ITEM, getCenter(), Item.SMALL_MEAT_RAW, Consts.ITEM_COOLDOWN_BREAK);
        }
    }

    @Override
    protected void plannedStarve() {}

    @Override
    protected void recalcFallDamage() {
        if(lastFallVelocity < -20 && getVelocity().y - lastFallVelocity > 19) {
            injure((int) (lastFallVelocity*(-0.5)));
        }
    }

    @Override
    public void writeInitClientMeta(PacketOutputStream out) {}

}
