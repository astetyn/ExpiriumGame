package com.astetyne.expirium.client.entity.animator;

import java.util.HashMap;

public enum EntityAnimation {

    INJURE, // followed by 1 float - damage value
    INTERACT; // not followed

    private int id;
    private static final HashMap<Integer, EntityAnimation> map;
    static {
        map = new HashMap<>();
        int i = 0;
        for(EntityAnimation it : EntityAnimation.values()) {
            it.id = i;
            map.put(it.id, it);
            i++;
        }
    }

    public static EntityAnimation getType(int id) {
        return map.get(id);
    }

    public int getId() {
        return id;
    }

}
