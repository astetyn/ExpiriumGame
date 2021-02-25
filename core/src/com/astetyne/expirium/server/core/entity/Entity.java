package com.astetyne.expirium.server.core.entity;

import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.client.entity.Metaable;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.WorldSaveable;
import com.astetyne.expirium.server.core.entity.player.Player;
import com.astetyne.expirium.server.core.world.file.WorldBuffer;
import com.astetyne.expirium.server.core.world.tile.Tile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class Entity implements Metaable, WorldSaveable {

    protected final ExpiServer server;
    private final short id;
    protected final EntityType type;
    protected final Body body;
    protected boolean destroyed;
    protected boolean inWater;
    protected final Vector2 tempVec; // just for optimization purpose

    public Entity(ExpiServer server, EntityType type, Vector2 loc) {
        this.server = server;
        this.type = type;
        destroyed = false;
        id = server.getRandomEntityID();
        inWater = false;
        tempVec = new Vector2();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(loc);
        body = server.getWorld().getB2dWorld().createBody(bodyDef);

        server.getEntities().add(this);
    }

    public Entity(ExpiServer server, EntityType type, DataInputStream in) throws IOException {
        this(server, type, new Vector2(in.readFloat(), in.readFloat()));
    }

    public void createBodyFixtures() {}

    public void onTick() {
        if(getLocation().y <= 0) destroy();
        recalcWater();
    }

    public void applyPhysics() {

        if(!inWater) return;

        float area = type.getWidth() * type.getHeight();
        tempVec.set(0, area * Consts.WATER_DENSITY * body.getWorld().getGravity().y * -1); // buoyant force
        body.applyForceToCenter(tempVec, true);
        tempVec.set(body.getLinearVelocity());
        tempVec.scl(0.05f);
        body.setLinearVelocity(body.getLinearVelocity().sub(tempVec.x * tempVec.x * tempVec.x, tempVec.y * tempVec.y * tempVec.y));

    }

    protected void recalcWater() {

        float w = getWidth();
        float h = getHeight();
        float wh = w/2;
        float hh = h/2;
        Vector2 center = getCenter();

        int leftX = (int) (center.x - wh);
        int bottomY = (int) (center.y - hh);

        // check if is in the water - only checks bottom overlapping tiles
        for(int x = leftX; x <= center.x + w; x++) {
            Tile t = server.getWorld().getTileAt(x, bottomY);
            if(t.getWaterLevel() == 0) continue;
            float th = (float)t.getWaterLevel() / Consts.MAX_WATER_LEVEL;
            if(bottomY + th >= center.y - hh) {
                inWater = true;
                return;
            }
        }
        inWater = false;
    }

    public short getId() {
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
        return body.getWorldCenter();
        //return centerLoc.set(body.getPosition().x + type.getWidth()/2, body.getPosition().y + type.getHeight()/2);
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

    public boolean isInWater() {
        return inWater;
    }

    public void setInWater(boolean inWater) {
        this.inWater = inWater;
    }

    public void destroy() {
        if(destroyed) return;
        destroyed = true;
        server.getEntities().remove(this);
        server.getWorld().getB2dWorld().destroyBody(body);
        for(Player pp : server.getPlayers()) {
            pp.getNetManager().putEntityDespawnPacket(this);
        }
    }

    public void writeData(WorldBuffer out) {
        out.writeFloat(body.getPosition().x);
        out.writeFloat(body.getPosition().y);
    }

}
