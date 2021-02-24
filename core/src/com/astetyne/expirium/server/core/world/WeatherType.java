package com.astetyne.expirium.server.core.world;

public enum WeatherType {

    SUNNY,
    RAIN,
    ;

    public static WeatherType get(int i) {
        return values()[i];
    }

}
