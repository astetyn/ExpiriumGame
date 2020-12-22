package com.astetyne.expirium.server.backend.packets;

import com.astetyne.expirium.main.utils.Constants;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.world.ExpiTile;
import com.astetyne.expirium.server.backend.Packable;
import com.badlogic.gdx.physics.box2d.Fixture;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ChunkDestroyPacket implements Packable {

    private final int chunkID;
    private final List<Integer> fixturesID;

    public ChunkDestroyPacket(ExpiTile[][] worldTerrain, int c) {

        this.chunkID = c;
        this.fixturesID = new ArrayList<>();

        for(int i = 0; i < Constants.T_H_CH; i++) {
            for(int j = 0; j < Constants.T_W_CH; j++) {
                ExpiTile et = worldTerrain[i][j+c*Constants.T_W_CH];
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
