package com.astetyne.expirium.client.world.input;

import java.util.HashMap;

public enum InteractType {

    PRESS,
    RELEASE,
    DRAG;

    int id;

    public static InteractType getType(int id) {
        return map.get(id);
    }

    public int getID() {
        return id;
    }

    private static final HashMap<Integer, InteractType> map;
    static {
        map = new HashMap<>();
        int i = 0;
        for(InteractType tt : InteractType.values()) {
            tt.id = i;
            map.put(tt.id, tt);
            i++;
        }
    }

}
