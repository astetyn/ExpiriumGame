package com.astetyne.main.net.netobjects;

import com.astetyne.main.net.server.entities.ServerEntity;

public class SPlayer extends SEntity {

    public SPlayer(ServerEntity entity) {
        super(entity.getID(), new SVector(entity.getLocation()));
    }

    public SPlayer(int id, SVector location) {
        super(id, location);
    }

}
