package com.astetyne.expirium.server.backend.packets;

import com.astetyne.expirium.main.utils.Constants;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.world.ExpiTile;
import com.astetyne.expirium.server.backend.FixturePack;
import com.astetyne.expirium.server.backend.Packable;
import com.astetyne.expirium.server.backend.packables.PackableFixture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class TilePlaceAckPacket implements Packable {

    private final int chunkID, x, y;
    private final int tileType;
    private final List<PackableFixture> newFixtures;
    private final List<Integer> destroyedFixtures;

    public TilePlaceAckPacket(ExpiTile t, FixturePack fp) {

        this.chunkID = t.getX() / Constants.T_W_CH;
        this.x = t.getX();
        this.y = t.getY();
        tileType = t.getType().getID();

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
        return 18;
    }

    @Override
    public void populateWithData(ByteBuffer bb) {
        bb.putInt(chunkID);
        bb.putInt(x);
        bb.putInt(y);
        bb.putInt(tileType);
        bb.putInt(newFixtures.size());
        for(PackableFixture pf : newFixtures) {
            pf.populateWithData(bb);
        }
        bb.putInt(destroyedFixtures.size());
        for(int e : destroyedFixtures) {
            bb.putInt(e);
        }
    }

}
