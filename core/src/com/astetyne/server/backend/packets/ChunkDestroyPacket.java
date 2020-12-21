package com.astetyne.server.backend.packets;

import com.astetyne.main.net.netobjects.ExpiChunk;
import com.astetyne.main.net.netobjects.ExpiTile;
import com.astetyne.main.utils.Constants;
import com.astetyne.server.GameServer;
import com.astetyne.server.backend.Packable;
import com.badlogic.gdx.physics.box2d.Fixture;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ChunkDestroyPacket implements Packable {

    private final int chunkID;
    private final List<Integer> fixturesID;

    public ChunkDestroyPacket(ExpiChunk chunk) {

        this.chunkID = chunk.getId();
        this.fixturesID = new ArrayList<>();

        for(int i = 0; i < Constants.T_H_CH; i++) {
            for(int j = 0; j < Constants.T_W_CH; j++) {
                ExpiTile et = chunk.getTerrain()[i][j];
                for(Fixture f : et.getFixtures()) {
                    fixturesID.add(GameServer.get().getWorld().getFixturesID().get(f));
                }
            }
        }
    }

    @Override
    public int getPacketID() {
        return 12;
    }

    @Override
    public void populateWithData(ByteBuffer bb) {
        bb.putInt(chunkID);
        bb.putInt(fixturesID.size());
        for(int e : fixturesID) {
            bb.putInt(e);
        }
    }
}
