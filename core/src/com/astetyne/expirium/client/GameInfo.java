package com.astetyne.expirium.client;

import java.util.HashMap;

public enum GameInfo {

    DISCONNECT; //todo: toto je nanic

    int id;

    public static GameInfo getType(int id) {
        return map.get(id);
    }

    public int getID() {
        return id;
    }

    private static final HashMap<Integer, GameInfo> map;
    static {
        map = new HashMap<>();
        int i = 0;
        for(GameInfo tt : GameInfo.values()) {
            tt.id = i;
            map.put(tt.id, tt);
            i++;
        }
    }

}
