package com.astetyne.main.entity;

import com.astetyne.main.entity.meta.DroppedEntityMeta;
import com.astetyne.main.entity.meta.EntityMeta;

import java.util.HashMap;

public enum EntityType {

    PLAYER(0, null),
    DROPPED_ITEM(1, DroppedEntityMeta.class);

    private static final HashMap<Integer, EntityType> map;

    static {
        map = new HashMap<>();
        for(EntityType et : EntityType.values()) {
            map.put(et.id, et);
        }
    }

    int id;
    Class<? extends EntityMeta> meta;

    EntityType(int id, Class<? extends EntityMeta> meta) {
        this.id = id;
        this.meta = meta;
    }

    public int getID() {
        return id;
    }

    public static EntityType getType(int id) {
        return map.get(id);
    }

}
