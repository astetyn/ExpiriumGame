package com.astetyne.expirium.server.core.entity;

import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.client.entity.Metaable;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.WorldSaveable;
import com.astetyne.expirium.server.core.entity.player.ExpiPlayer;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class ExpiEntity implements Metaable, WorldSaveable {

    protected final ExpiServer server;
    private final int id;
    protected final EntityType type;
    private final Vector2 centerLoc;
    protected final Body body;
    protected boolean destroyed;

    public ExpiEntity(ExpiServer server, EntityType type, Vector2 loc) {
        this.server = server;
        destroyed = false;
        id = server.getRandomEntityID();
        this.type = type;
        centerLoc = new Vector2();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(loc);
        body = server.getWorld().getB2dWorld().createBody(bodyDef);

        server.getEntities().add(this);
    }

    public ExpiEntity(ExpiServer server, EntityType type,  DataInputStream in) throws IOException {
        this(server, type, new Vector2(in.readFloat(), in.readFloat()));
    }

    public void createBodyFixtures() {}

    public void onTick() {
        if(getLocation().y <= 0) destroy();
    }

    public int getId() {
        return id;
    }

    public Vector2 getLocation() {
        return body.getPosition();
    }

    /** Note that this method is unsafe and teleport place must be checked in advance. */
    public void teleport(float x, float y) {
        body.setTransform(x, y, 0);
    }

    public Vector2 getCenter() {
        return centerLoc.set(body.getPosition().x + type.getWidth()/2, body.getPosition().y + type.getHeight()/2);
    }

    public Vector2 getVelocity() {
        return body.getLinearVelocity();
    }

    public float getWidth() {
        return type.getWidth();
    }

    public float getHeight() {
        return type.getHeight();
    }

    public EntityType getType() {
        return type;
    }

    public Body getBody() {
        return body;
    }

    public ExpiServer getServer() {
        return server;
    }

    public boolean isLookingRight() {
        return true;
    }

    public void destroy() {
        if(destroyed) return;
        destroyed = true;
        server.getEntities().remove(this);
        server.getWorld().getB2dWorld().destroyBody(body);
        for(ExpiPlayer pp : server.getPlayers()) {
            pp.getNetManager().putEntityDespawnPacket(this);
        }
    }

    public void writeData(WorldBuffer out) {
        out.writeFloat(body.getPosition().x);
        out.writeFloat(body.getPosition().y);
    }

}
