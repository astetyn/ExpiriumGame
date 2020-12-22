package com.astetyne.expirium.server.backend.packets;

import com.astetyne.expirium.server.backend.Packable;
import com.astetyne.expirium.server.backend.packables.PackableEntity;
import com.badlogic.gdx.math.Vector2;

import java.nio.ByteBuffer;
import java.util.List;

public class InitDataPacket implements Packable {

    private final int numberOfChunks;
    private final int playerID;
    private final Vector2 location;
    private final List<PackableEntity> entities;

    public InitDataPacket(int numberOfChunks, int playerID, Vector2 location, List<PackableEntity> entities) {
        this.numberOfChunks = numberOfChunks;
        this.playerID = playerID;
        this.location = location;
        this.entities = entities;
    }

    @Override
    public int getPacketID() {
        return 11;
    }

    @Override
    public void populateWithData(ByteBuffer bb) {
        bb.putInt(numberOfChunks);
        bb.putInt(playerID);
        bb.putFloat(location.x);
        bb.putFloat(location.y);
        bb.putInt(entities.size());
        for(PackableEntity ped : entities) {
            ped.populateWithData(bb);
        }
    }
}
