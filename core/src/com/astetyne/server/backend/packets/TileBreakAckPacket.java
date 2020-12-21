package com.astetyne.server.backend.packets;

import com.astetyne.server.GameServer;
import com.astetyne.server.backend.FixturePack;
import com.astetyne.server.backend.Packable;
import com.astetyne.server.backend.packables.PackableFixture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class TileBreakAckPacket implements Packable {

    private final int chunkID, x, y;
    private final List<PackableFixture> newFixtures;
    private final List<Integer> destroyedFixtures;

    public TileBreakAckPacket(int chunkID, int x, int y, FixturePack fp) {

        this.chunkID = chunkID;
        this.x = x;
        this.y = y;

        newFixtures = new ArrayList<>();
        Vector2 v1 = new Vector2();
        Vector2 v2 = new Vector2();

        for(Fixture f : fp.addedFixtures) {
            EdgeShape shape = (EdgeShape) f.getShape();
            shape.getVertex1(v1);
            shape.getVertex2(v2);
            int fixID = GameServer.get().getWorld().getFixturesID().get(f);
            newFixtures.add(new PackableFixture(fixID, v1.x, v1.y, v2.x, v2.y));
        }
        destroyedFixtures = fp.removedFixtures;
    }

    @Override
    public int getPacketID() {
        return 17;
    }

    @Override
    public void populateWithData(ByteBuffer bb) {
        bb.putInt(chunkID);
        bb.putInt(x);
        bb.putInt(y);
        bb.putInt(newFixtures.size());
        for(PackableFixture pf : newFixtures) {
            bb.putInt(pf.id);
            bb.putFloat(pf.x1);
            bb.putFloat(pf.y1);
            bb.putFloat(pf.x2);
            bb.putFloat(pf.y2);
        }
        bb.putInt(destroyedFixtures.size());
        for(int e : destroyedFixtures) {
            bb.putInt(e);
        }
    }
}
