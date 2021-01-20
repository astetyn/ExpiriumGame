package com.astetyne.expirium.server.net;

import java.util.HashMap;

public enum SimpleServerPacket {

    CLOSE_DOUBLE_INV,
    DEATH_EVENT;

    public static SimpleServerPacket getType(int id) {
        return map.get(id);
    }

    public int getID() {
        return id;
    }

    int id;
    private static final HashMap<Integer, SimpleServerPacket> map;
    static {
        map = new HashMap<>();
        int i = 0;
        for(SimpleServerPacket fap : SimpleServerPacket.values()) {
            fap.id = i;
            map.put(fap.id, fap);
            i++;
        }
    }

}
