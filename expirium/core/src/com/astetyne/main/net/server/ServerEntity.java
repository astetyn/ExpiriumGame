package com.astetyne.main.net.server;

import com.badlogic.gdx.math.Vector2;

public class ServerEntity {

    private final int ID;
    private final Vector2 location;

    public ServerEntity(Vector2 location) {

        int randomID;
        do {
            randomID = (int)(Math.random()*Integer.MAX_VALUE);
        } while(GameServer.getServer().getEntitiesID().containsKey(randomID));
        GameServer.getServer().getEntitiesID().put(randomID, this);

        this.ID = randomID;
        this.location = location;
    }

    public int getID() {
        return ID;
    }

    public Vector2 getLocation() {
        return location;
    }
}
