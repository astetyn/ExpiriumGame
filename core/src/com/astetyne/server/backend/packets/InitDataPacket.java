package com.astetyne.server.backend.packets;

import com.astetyne.server.backend.Packable;
import com.astetyne.server.backend.packables.PackableEntity;
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
    public byte[] toByteArray() {
        ByteBuffer bb = ByteBuffer.allocate(4 + 8 + 8 + 4 + 4*4*entities.size());
        bb.putInt(getPacketID());
        bb.putInt(numberOfChunks);
        bb.putInt(playerID);
        bb.putFloat(location.x);
        bb.putFloat(location.y);
        bb.putInt(entities.size());
        for(PackableEntity ped : entities) {
            bb.putInt(ped.id);
            bb.putInt(ped.type);
            bb.putFloat(ped.x);
            bb.putFloat(ped.y);
        }
        return bb.array();
    }
}
