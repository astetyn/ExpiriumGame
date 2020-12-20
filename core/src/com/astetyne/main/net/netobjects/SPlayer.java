package com.astetyne.main.net.netobjects;

import com.astetyne.server.api.entities.ExpiEntity;

public class SPlayer extends SEntity {

    public SPlayer(ExpiEntity entity) {
        super(entity.getID(), new SVector(entity.getLocation()));
    }

    public SPlayer(int id, SVector location) {
        super(id, location);
    }

}
