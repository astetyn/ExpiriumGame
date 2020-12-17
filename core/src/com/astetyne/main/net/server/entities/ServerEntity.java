package com.astetyne.main.net.server.entities;

import com.astetyne.main.net.server.GameServer;
import com.badlogic.gdx.math.Vector2;

public class ServerEntity {

    private final int ID;
    private final Vector2 location;
    private final Vector2 velocity;
    private final float width, height;
    private float angle;

    public ServerEntity(Vector2 location, float width, float height) {

        int randomID;
        do {
            randomID = (int)(Math.random()*Integer.MAX_VALUE);
        } while(GameServer.getServer().getEntitiesID().containsKey(randomID));
        GameServer.getServer().getEntitiesID().put(randomID, this);

        this.ID = randomID;
        this.location = location;
        velocity = new Vector2();
        this.width = width;
        this.height = height;
        this.angle = 0;
    }

    public int getID() {
        return ID;
    }

    public Vector2 getLocation() {
        return location;
    }

    public Vector2 getCenter() {
        return new Vector2(location.x + width/2, location.y + height/2);
    }

    public Vector2 getVelocity() {
        return velocity;
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
}
