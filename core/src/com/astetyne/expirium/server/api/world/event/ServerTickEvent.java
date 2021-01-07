package com.astetyne.expirium.server.api.world.event;

import java.util.HashSet;

public class ServerTickEvent {

    private static final HashSet<TickListener> listeners = new HashSet<>();

    public static void onTick() {
        HashSet<TickListener> copy = new HashSet<>(listeners); // todo: toto je dost drahe na kazdy tick
        for(TickListener listener : copy) {
            listener.onTick();
        }
    }

    public static HashSet<TickListener> getListeners() {
        return listeners;
    }

}
