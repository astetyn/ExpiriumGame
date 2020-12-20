package com.astetyne.main.entity;

import java.util.HashMap;

public enum EntityType {

    PLAYER(0),
    DROPPED_ITEM(1);

    private static final HashMap<Integer, EntityType> map;

    static {
        map = new HashMap<>();
        for(EntityType et : EntityType.values()) {
            map.put(et.id, et);
        }
    }

    int id;

    EntityType(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public static EntityType getType(int id) {
        return map.get(id);
    }

}
