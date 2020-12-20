package com.astetyne.server.backend.packables;

import com.astetyne.main.entity.EntityType;
import com.astetyne.server.api.entities.ExpiEntity;

public class PackableEntity {

    public int id;
    public float x, y;
    public int type;

    public PackableEntity(int id, float x, float y, EntityType type) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.type = type.getID();
    }
    public PackableEntity(ExpiEntity e) {
        this.id = e.getID();
        this.x = e.getLocation().x;
        this.y = e.getLocation().y;
        this.type = e.getType().getID();
    }

}
