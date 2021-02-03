package com.astetyne.expirium.server;

import com.astetyne.expirium.server.core.world.generator.WorldPreferences;

public class ServerPreferences {

    public final WorldPreferences worldPreferences;
    public final int tps;
    public final int port;

    public ServerPreferences(WorldPreferences worldPreferences, int tps, int port) {
        this.worldPreferences = worldPreferences;
        this.tps = tps;
        this.port = port;
    }
}
