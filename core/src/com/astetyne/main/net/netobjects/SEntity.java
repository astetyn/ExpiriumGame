package com.astetyne.main.net.netobjects;

import com.astetyne.main.net.server.entities.ServerEntity;

import java.io.Serializable;

public class SEntity implements Serializable {

    private final int ID;
    transient private final SVector location;

    public SEntity(ServerEntity entity) {
        this.ID = entity.getID();
        this.location = new SVector(entity.getLocation());
    }

    public SEntity(int ID, SVector location) {
        this.ID = ID;
        this.location = location;
    }

    public int getID() {
        return ID;
    }

    public SVector getLocation() {
        return location;
    }
}
