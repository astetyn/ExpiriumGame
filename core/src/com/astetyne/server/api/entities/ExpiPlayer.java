package com.astetyne.server.api.entities;

import com.astetyne.main.entity.EntityType;
import com.astetyne.server.backend.ServerPlayerGateway;
import com.badlogic.gdx.math.Vector2;

import java.nio.ByteBuffer;
import java.util.HashSet;

public class ExpiPlayer extends ExpiEntity {

    private final ServerPlayerGateway gateway;
    private String name;
    private final HashSet<Integer> activeChunks;

    public ExpiPlayer(Vector2 location, ServerPlayerGateway gateway, String name) {
        super(EntityType.PLAYER, location, 0.9f, 1.25f);
        this.gateway = gateway;
        this.name = name;
        activeChunks = new HashSet<>();
    }

    public void onMove(float x, float y, float v1, float v2) {

        body.setTransform(x, y, 0);
        body.setLinearVelocity(v1, v2);
        //todo: oznamit to aj ostatnym

    }

    public ServerPlayerGateway getGateway() {
        return gateway;
    }

    public String getName() {
        return name;
    }

    public HashSet<Integer> getActiveChunks() {
        return activeChunks;
    }

    @Override
    public EntityType getType() {
        return EntityType.PLAYER;
    }

    @Override
    public void readMeta(ByteBuffer bb) {
        int nameLen = bb.getInt();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < nameLen; i++) {
            sb.append(bb.getChar());
        }
        name = sb.toString();
    }

    @Override
    public void writeMeta(ByteBuffer bb) {
        bb.putInt(name.length());
        for(char c : name.toCharArray()) {
            bb.putChar(c);
        }
    }
}
