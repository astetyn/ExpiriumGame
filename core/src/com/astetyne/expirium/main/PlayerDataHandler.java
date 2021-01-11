package com.astetyne.expirium.main;

import com.astetyne.expirium.server.backend.PacketInputStream;

public class PlayerDataHandler {

    private float health, food, temperature;

    public void feed(PacketInputStream in) {
        health = in.getFloat();
        food = in.getFloat();
        temperature = in.getFloat();
    }

    public float getHealth() {
        return health;
    }

    public float getFood() {
        return food;
    }

    public float getTemperature() {
        return temperature;
    }
}
