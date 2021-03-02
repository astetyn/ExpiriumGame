package com.astetyne.expirium.server.core.world;

public enum WeatherType {

    SUN,
    RAIN,
    ;

    public static WeatherType get(int i) {
        return values()[i];
    }

}
