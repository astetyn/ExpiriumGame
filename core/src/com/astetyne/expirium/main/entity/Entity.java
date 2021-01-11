package com.astetyne.expirium.main.entity;

import com.astetyne.expirium.main.screens.GameScreen;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity implements MetaReadable {

    protected EntityType type;
    protected int ID;
    protected final Vector2 location;
    protected float angle;
    protected final Vector2 velocity;
    private final Vector2 targetLocation;
    private float interpolationDelta;
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
        interpolationDelta = 0;
        targetAngle = 0;
        location = loc;
        targetLocation = loc.cpy();
        centerLoc = loc.cpy();
        lastLoc = loc.cpy();
        velocity = new Vector2();
        lastAngle = 0;

        GameScreen.get().getWorld().getEntitiesID().put(ID, this);
        GameScreen.get().getWorld().getEntities().add(this);

    }

    public abstract void draw();

    public void move() {

        if(interpolationDelta == -1) return;

        interpolationDelta += GameScreen.get().getServerTPS() / (float) Gdx.graphics.getFramesPerSecond();

        float posX = lastLoc.x + (targetLocation.x - lastLoc.x) * interpolationDelta;
        float posY = lastLoc.y + (targetLocation.y - lastLoc.y) * interpolationDelta;
        float ang = lastAngle + (targetAngle-lastAngle) * interpolationDelta;

        location.set(posX, posY);
        angle = ang;

        if(interpolationDelta >= 1) {
            location.set(targetLocation);
            angle = targetAngle;
            interpolationDelta = -1;
        }
    }

    public void onMove(PacketInputStream in) {
        targetLocation.set(in.getFloat(), in.getFloat());
        velocity.set(in.getFloat(), in.getFloat());
        targetAngle = in.getFloat();
        lastLoc = getLocation().cpy();
        lastAngle = angle;
        interpolationDelta = 0;
        System.out.println("C: onMove: x: "+targetLocation.x+" y: "+targetLocation.y);
    }

    public Vector2 getLocation() {
        return location;
    }

    public Vector2 getCenter() {
        return centerLoc.set(getLocation()).add(width/2, height/2);
    }

    public int getID() {
        return ID;
    }

    public void destroy() {
        GameScreen.get().getWorld().getEntitiesID().remove(ID);
        GameScreen.get().getWorld().getEntities().remove(this);
    }

    public EntityType getType() {
        return type;
    }

    public Vector2 getVelocity() {
        return velocity;
    }
}
