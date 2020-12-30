package com.astetyne.expirium.main.entity;

import com.astetyne.expirium.main.stages.GameStage;
import com.astetyne.expirium.main.utils.Constants;
import com.astetyne.expirium.main.world.Collidable;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

public abstract class Entity implements Collidable, MetaReadable {

    protected EntityType type;
    protected final int ID;
    protected Body body;
    private final Vector2 targetPosition;
    private float intpolDelta;
    private float targetAngle;
    protected boolean onGround;
    private int collisions;
    protected final float width, height;
    private final Vector2 centerLoc;

    public Entity(EntityType type, int id, Vector2 loc, float width, float height) {

        this.type = type;
        this.ID = id;
        this.width = width;
        this.height = height;
        body = EntityBodyFactory.createBody(type, loc, GameStage.get().getWorld().getB2dWorld());
        intpolDelta = 0;
        targetAngle = 0;
        onGround = false;
        collisions = 0;
        targetPosition = loc;
        centerLoc = new Vector2();

        GameStage.get().getWorld().getEntitiesID().put(ID, this);
        GameStage.get().getWorld().getEntities().add(this);

    }

    public abstract void draw();

    public void move() {

        if(intpolDelta == -1) return;

        float ang = body.getAngle();

        body.setTransform(getLocation().lerp(targetPosition.cpy(), intpolDelta), ang + (targetAngle-ang)*intpolDelta);
        intpolDelta = intpolDelta + 1.0f / Constants.SERVER_DEFAULT_TPS;
        if(intpolDelta >= 1) {
            body.setTransform(targetPosition, targetAngle);
            intpolDelta = -1;
        }
    }

    public void onMove(PacketInputStream in) {
        targetPosition.set(in.getFloat(), in.getFloat());
        intpolDelta = 0;
        body.setLinearVelocity(in.getFloat(), in.getFloat());
        targetAngle = in.getFloat();
        body.setAngularVelocity(in.getFloat());
    }

    public Vector2 getLocation() {
        return body.getPosition();
    }

    public Vector2 getCenterLocation() {
        return centerLoc.set(getLocation()).add(width/2, height/2);
    }

    public Vector2 getVelocity() {
        return body.getLinearVelocity();
    }

    public Body getBody() {
        return body;
    }

    public int getID() {
        return ID;
    }

    public Vector2 getTargetPosition() {
        return targetPosition;
    }

    public void destroy() {
        GameStage.get().getWorld().getEntitiesID().remove(ID);
        GameStage.get().getWorld().getEntities().remove(this);
        GameStage.get().getWorld().getB2dWorld().destroyBody(body);
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

    public EntityType getType() {
        return type;
    }
}
