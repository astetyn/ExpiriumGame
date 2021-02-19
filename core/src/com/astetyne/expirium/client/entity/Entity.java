package com.astetyne.expirium.client.entity;

import com.astetyne.expirium.client.entity.animator.EntityAnimator;
import com.astetyne.expirium.client.screens.GameScreen;
import com.astetyne.expirium.client.tiles.Tile;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {

    protected EntityType type;
    protected int ID;
    protected final Vector2 location;
    protected float angle;
    protected final Vector2 velocity;
    private final Vector2 targetLocation;
    private float interpolationDelta;
    private float targetAngle;
    private final Vector2 centerLoc;
    private Vector2 lastLoc;
    private float lastAngle;
    private EntityAnimator animator;
    private boolean lookingRight;
    private boolean active;

    public Entity(EntityType type, int id, Vector2 loc) {

        this.type = type;
        this.ID = id;
        this.animator = new EntityAnimator(this) {
            @Override
            public void draw(SpriteBatch batch) {}
        };
        interpolationDelta = 0;
        targetAngle = 0;
        location = loc;
        targetLocation = loc.cpy();
        centerLoc = loc.cpy();
        lastLoc = loc.cpy();
        velocity = new Vector2();
        lastAngle = 0;
        lookingRight = true;
        active = true;

        GameScreen.get().getWorld().getEntitiesID().put(ID, this);
        GameScreen.get().getWorld().getEntities().add(this);

    }

    public void update() {
        move();
        animator.update();
    }

    public void draw(SpriteBatch batch) {
        animator.draw(batch);
    }

    private void move() {

        if(interpolationDelta == -1) return;

        //interpolationDelta += Consts.SERVER_TPS / (float) Gdx.graphics.getFramesPerSecond();
        interpolationDelta += Consts.SERVER_TPS * Gdx.graphics.getDeltaTime();

        if(interpolationDelta >= 1) {
            location.set(targetLocation);
            angle = targetAngle;
            interpolationDelta = -1;
        }else {
            float posX = lastLoc.x + (targetLocation.x - lastLoc.x) * interpolationDelta;
            float posY = lastLoc.y + (targetLocation.y - lastLoc.y) * interpolationDelta;
            float ang = lastAngle + (targetAngle-lastAngle) * interpolationDelta;

            location.set(posX, posY);
            angle = ang;
        }
    }

    public void onMove(PacketInputStream in) {
        targetLocation.set(in.getFloat(), in.getFloat());
        velocity.set(in.getFloat(), in.getFloat());
        targetAngle = in.getFloat();
        lookingRight = in.getBoolean();
        lastLoc = getLocation().cpy();
        lastAngle = angle;
        interpolationDelta = 0;
        active = true;
    }

    public Vector2 getLocation() {
        return location;
    }

    public Vector2 getCenter() {
        return centerLoc.set(getLocation()).add(type.width/2, type.height/2);
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

    public Tile getCenterTile() {
        Vector2 center = getCenter();
        return GameScreen.get().getWorld().getTileAt(center.x, center.y);
    }

    public void setAnimator(EntityAnimator animator) {
        this.animator = animator;
    }

    public EntityAnimator getAnimator() {
        return animator;
    }

    public float getAngle() {
        return angle;
    }

    public boolean isLookingRight() {
        return lookingRight;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
