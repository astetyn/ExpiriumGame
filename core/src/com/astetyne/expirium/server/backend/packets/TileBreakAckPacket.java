package com.astetyne.expirium.server.backend.packets;

import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.world.ExpiTile;
import com.astetyne.expirium.server.backend.FixturePack;
import com.astetyne.expirium.server.backend.Packable;
import com.astetyne.expirium.server.backend.packables.PackableChangedTile;
import com.astetyne.expirium.server.backend.packables.PackableFixture;
import com.astetyne.expirium.server.backend.packables.PackableStabilityChange;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TileBreakAckPacket implements Packable {

    private final List<PackableChangedTile> brokenTiles;
    private final List<PackableFixture> newFixtures;
    private final List<Integer> destroyedFixtures;
    private final List<PackableStabilityChange> affectedTiles;

    public TileBreakAckPacket(List<PackableChangedTile> brokenTiles, FixturePack fp, HashSet<ExpiTile> at) {

        this.brokenTiles = brokenTiles;

        affectedTiles = new ArrayList<>();

        for(ExpiTile et : at) {
            affectedTiles.add(new PackableStabilityChange(et));
        }

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
        bb.putInt(brokenTiles.size());
        for(PackableChangedTile pbt : brokenTiles) {
            pbt.populateWithData(bb);
        }
        bb.putInt(newFixtures.size());
        for(PackableFixture pf : newFixtures) {
            pf.populateWithData(bb);
        }
        bb.putInt(destroyedFixtures.size());
        for(int e : destroyedFixtures) {
            bb.putInt(e);
        }
        bb.putInt(affectedTiles.size());
        for(PackableStabilityChange psch : affectedTiles) {
            psch.populateWithData(bb);
        }
    }
}
