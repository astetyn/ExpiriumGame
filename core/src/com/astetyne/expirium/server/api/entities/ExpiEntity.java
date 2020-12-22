package com.astetyne.expirium.server.api.entities;

import com.astetyne.expirium.main.entity.EntityBodyFactory;
import com.astetyne.expirium.main.entity.EntityType;
import com.astetyne.expirium.main.entity.Metaable;
import com.astetyne.expirium.server.GameServer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public abstract class ExpiEntity implements Metaable {

    private final int ID;
    private final float width, height;
    private float angle;
    protected EntityType type;
    protected Body body;

    public ExpiEntity(EntityType type, Vector2 loc, float width, float height) {

        this.type = type;
        body = EntityBodyFactory.createBody(type, loc, GameServer.get().getWorld().getBox2dWorld());

        int randomID;
        do {
            randomID = (int)(Math.random()*Integer.MAX_VALUE);
        } while(GameServer.get().getEntitiesID().containsKey(randomID));
        GameServer.get().getEntitiesID().put(randomID, this);
        GameServer.get().getEntities().add(this);

        this.ID = randomID;
        this.width = width;
        this.height = height;
        this.angle = 0;
    }

    public int getID() {
        return ID;
    }

    public Vector2 getLocation() {
        return body.getPosition();
    }

    public Vector2 getCenter() {
        return new Vector2(body.getPosition().x + width/2, body.getPosition().y + height/2);
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
        GameServer.get().getEntitiesID().remove(ID);
        GameServer.get().getEntities().remove(this);
        GameServer.get().getWorld().getBox2dWorld().destroyBody(body);
    }

}
