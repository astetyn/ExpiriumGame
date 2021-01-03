package com.astetyne.expirium.main.entity;

import com.astetyne.expirium.main.stages.GameStage;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public abstract class Entity implements MetaReadable {

    protected EntityType type;
    protected final int ID;
    protected Body body;
    private final Vector2 targetPosition;
    private float intpolDelta;
    private float targetAngle;
    protected final float width, height;
    private final Vector2 centerLoc;
    private Vector2 lastLoc;
    private float lastAngle;

    public Entity(EntityType type, int id, Vector2 loc, float width, float height) {

        this.type = type;
        this.ID = id;
        this.width = width;
        this.height = height;
        body = EntityBodyFactory.createBody(type, loc, GameStage.get().getWorld().getB2dWorld());
        intpolDelta = 0;
        targetAngle = 0;
        targetPosition = loc;
        centerLoc = new Vector2();
        lastLoc = new Vector2();
        lastAngle = 0;

        GameStage.get().getWorld().getEntitiesID().put(ID, this);
        GameStage.get().getWorld().getEntities().add(this);

    }

    public abstract void draw();

    public void move() {

        if(intpolDelta == -1) return;


        float posX = lastLoc.x + (targetPosition.x - lastLoc.x) * intpolDelta;
        float posY = lastLoc.y + (targetPosition.y - lastLoc.y) * intpolDelta;
        float ang = lastAngle + (targetAngle-lastAngle) * intpolDelta;

        intpolDelta += Consts.SERVER_DEFAULT_TPS / (float) Gdx.graphics.getFramesPerSecond();

        body.setTransform(posX, posY, ang);

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
        lastLoc = getLocation().cpy();
        lastAngle = body.getAngle();
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

    public EntityType getType() {
        return type;
    }
}
