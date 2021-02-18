package com.astetyne.expirium.server.core.entity;

import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.net.PacketOutputStream;
import com.badlogic.gdx.math.Vector2;

import java.io.DataInputStream;
import java.io.IOException;

public class Squirrel extends LivingEntity {

    public Squirrel(ExpiServer server, Vector2 loc) {
        super(server, EntityType.SQUIRREL, loc);
    }

    public Squirrel(ExpiServer server, DataInputStream in) throws IOException {
        super(server, EntityType.SQUIRREL, in);
    }

    @Override
    public void onTick() {
        super.onTick();
        if(server.getWorld().getTick() % 160 == (int) (Math.random()*160)) {
            if(Math.random() > 0.5) {
                body.applyLinearImpulse((float) (Math.random()*200), (float) (Math.random()*200), getCenter().x, getCenter().y, true);
            }else {
                body.applyLinearImpulse((float) -(Math.random()*200), (float) (Math.random()*200), getCenter().x, getCenter().y, true);
            }
        }
    }

    @Override
    public void writeInitClientMeta(PacketOutputStream out) {}

}