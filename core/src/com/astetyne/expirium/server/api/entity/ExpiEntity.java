package com.astetyne.expirium.server.api.entity;

import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.client.entity.Metaable;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.api.Saveable;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

public abstract class ExpiEntity implements Metaable, Collidable, Saveable {

    private final int ID;
    private final float width, height;
    private float angle;
    protected EntityType type;
    protected Body body;
    protected boolean onGround;
    private int collisions;
    private final Vector2 centerLoc;

    public ExpiEntity(EntityType type, float width, float height) {

        this.type = type;

        centerLoc = new Vector2();

        int randomID;
        do {
            randomID = (int)(Math.random()*Integer.MAX_VALUE);
        } while(ExpiServer.get().getEntitiesID().containsKey(randomID));
        ExpiServer.get().getEntitiesID().put(randomID, this);
        ExpiServer.get().getEntities().add(this);

        this.ID = randomID;
        this.width = width;
        this.height = height;
        this.angle = 0;
        onGround = false;
        collisions = 0;
    }

    public int getID() {
        return ID;
    }

    public Vector2 getLocation() {
        return body.getPosition();
    }

    public Vector2 getCenter() {
        return centerLoc.set(body.getPosition().x + width/2, body.getPosition().y + height/2);
    }

    public Vector2 getVelocity() {
        return body.getLinearVelocity();
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float getAngle() {
        return angle;
    }

    public EntityType getType() {
        return type;
    }

    public Body getBody() {
        return body;
    }

    public void destroy() {
        ExpiServer.get().getEntitiesID().remove(ID);
        ExpiServer.get().getEntities().remove(this);
        ExpiServer.get().getWorld().getB2dWorld().destroyBody(body);
    }

    @Override
    public void onCollisionBegin(Fixture fix) {
        collisions++;
        onGround = true;
    }

    @Override
    public void onCollisionEnd(Fixture fix) {
        collisions--;
        if(collisions == 0) onGround = false;
    }

}
