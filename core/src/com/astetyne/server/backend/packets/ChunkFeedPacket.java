package com.astetyne.server.backend.packets;

import com.astetyne.main.net.netobjects.ExpiChunk;
import com.astetyne.main.net.netobjects.ExpiTile;
import com.astetyne.main.utils.Constants;
import com.astetyne.server.GameServer;
import com.astetyne.server.backend.Packable;
import com.astetyne.server.backend.packables.PackableFixture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ChunkFeedPacket implements Packable {

    private final int chunkID;
    private final int[][] terrain;
    private final List<PackableFixture> fixtures;

    public ChunkFeedPacket(ExpiChunk chunk) {

        chunkID = chunk.getId();
        terrain = new int[Constants.T_H_CH][Constants.T_W_CH];
        fixtures = new ArrayList<>();

        Vector2 v1 = new Vector2();
        Vector2 v2 = new Vector2();

        for(int i = 0; i < Constants.T_H_CH; i++) {
            for(int j = 0; j < Constants.T_W_CH; j++) {
                ExpiTile et = chunk.getTerrain()[i][j];
                terrain[i][j] = et.getType().getID();
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
    public byte[] toByteArray() {

        ByteBuffer bb = ByteBuffer.allocate(4 + 4 + Constants.T_H_CH*Constants.T_W_CH*4 + 4 + fixtures.size()*5*4);
        bb.putInt(getPacketID());
        bb.putInt(chunkID);
        for(int i = 0; i < Constants.T_H_CH; i++) {
            for(int j = 0; j < Constants.T_W_CH; j++) {
                bb.putInt(terrain[i][j]);
            }
        }
        bb.putInt(fixtures.size());
        for(PackableFixture pf : fixtures) {
            bb.putInt(pf.id);
            bb.putFloat(pf.x1);
            bb.putFloat(pf.y1);
            bb.putFloat(pf.x2);
            bb.putFloat(pf.y2);
        }
        return bb.array();
    }
}
