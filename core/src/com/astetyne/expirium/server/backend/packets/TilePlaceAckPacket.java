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
import java.util.HashSet;
import java.util.List;

public class TilePlaceAckPacket implements Packable {

    private final HashSet<ExpiTile> changedTiles;
    private final ExpiTile changedTile;
    private final int tileType;
    private final List<PackableFixture> newFixtures;
    private final List<Integer> destroyedFixtures;

    public TilePlaceAckPacket(ExpiTile t, FixturePack fp, HashSet<ExpiTile> changedTiles) {

        tileType = t.getType().getID();
        changedTile = t;

        this.changedTiles = changedTiles;

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
        bb.putInt(tileType);
        bb.putInt(changedTile.getX() / Constants.T_W_CH);
        bb.putInt(changedTile.getX());
        bb.putInt(changedTile.getY());
        bb.putInt(changedTiles.size());
        for(ExpiTile t : changedTiles) {
            bb.putInt(t.getX() / Constants.T_W_CH);
            bb.putInt(t.getX());
            bb.putInt(t.getY());
            bb.putInt(t.getStability());
        }
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
