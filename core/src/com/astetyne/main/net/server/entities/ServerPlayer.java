package com.astetyne.main.net.server.entities;

import com.astetyne.main.net.server.ServerPlayerGateway;
import com.badlogic.gdx.math.Vector2;

public class ServerPlayer extends ServerEntity {

    private final ServerPlayerGateway gateway;
    private final String name;

    public ServerPlayer(Vector2 location, ServerPlayerGateway gateway, String name) {
        super(location, 0.9f, 1.25f);
        this.gateway = gateway;
        this.name = name;
    }

    public ServerPlayerGateway getGateway() {
        return gateway;
    }

    public String getName() {
        return name;
    }
}
