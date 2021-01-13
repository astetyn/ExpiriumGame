package com.astetyne.expirium.server.api.world;

import java.util.HashMap;

public enum WeatherType {

    SUNNY,
    RAIN;

    int id;

    private static final HashMap<Integer, WeatherType> map;
    static {
        map = new HashMap<>();
        int i = 0;
        for(WeatherType wt : WeatherType.values()) {
            wt.id = i;
            map.put(wt.id, wt);
            i++;
        }
    }

    public int getID() {
        return id;
    }

    public static WeatherType getType(int id) {
        return map.get(id);
    }

}
