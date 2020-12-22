package com.astetyne.expirium.server.backend.packets;

import com.astetyne.expirium.main.utils.Constants;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.world.ExpiTile;
import com.astetyne.expirium.server.backend.Packable;
import com.astetyne.expirium.server.backend.packables.PackableFixture;
import com.astetyne.expirium.server.backend.packables.PackableTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ChunkFeedPacket implements Packable {

    private final int chunkID;
    private final PackableTile[][] chunkTerrain;
    private final List<PackableFixture> fixtures;

    public ChunkFeedPacket(ExpiTile[][] worldTerrain, int c) {

        chunkID = c;
        chunkTerrain = new PackableTile[Constants.T_H_CH][Constants.T_W_CH];
        fixtures = new ArrayList<>();

        Vector2 v1 = new Vector2();
        Vector2 v2 = new Vector2();

        for(int i = 0; i < Constants.T_H_CH; i++) {
            for(int j = 0; j < Constants.T_W_CH; j++) {
                ExpiTile et = worldTerrain[i][j+c*Constants.T_W_CH];
                chunkTerrain[i][j] = new PackableTile((byte)et.getType().getID(), (byte)et.getStability());
                for(Fixture f : et.getFixtures()) {
                    EdgeShape shape = (EdgeShape) f.getShape();
                    shape.getVertex1(v1);
                    shape.getVertex2(v2);
                    int fixID = GameServer.get().getWorld().getFixturesID().get(f);
                    fixtures.add(new PackableFixture(fixID, v1.x, v1.y, v2.x, v2.y));
                }
            }
        }
    }

    @Override
    public int getPacketID() {
        return 13;
    }

    @Override
    public void populateWithData(ByteBuffer bb) {
        bb.putInt(chunkID);
        for(int i = 0; i < Constants.T_H_CH; i++) {
            for(int j = 0; j < Constants.T_W_CH; j++) {
                chunkTerrain[i][j].populateWithData(bb);
            }
        }
        bb.putInt(fixtures.size());
        for(PackableFixture pf : fixtures) {
            pf.populateWithData(bb);
        }
    }
}
