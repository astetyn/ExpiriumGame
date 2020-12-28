package com.astetyne.expirium.server.backend;

import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.utils.Constants;
import com.astetyne.expirium.main.utils.IntVector2;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.ExpiInventory;
import com.astetyne.expirium.server.api.entities.ExpiEntity;
import com.astetyne.expirium.server.api.entities.ExpiPlayer;
import com.astetyne.expirium.server.api.world.ExpiTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ServerPacketManager {

    private final PacketInputStream in;
    private final PacketOutputStream out;

    public ServerPacketManager(PacketInputStream in, PacketOutputStream out) {
        this.in = in;
        this.out = out;
    }

    public void putInitDataPacket(int numberOfChunks, ExpiPlayer p, List<ExpiEntity> entities) {
        out.startPacket(11);
        out.putInt(p.getInv().getId());
        out.putInt(numberOfChunks);
        out.putInt(p.getID());
        out.putFloat(p.getLocation().x);
        out.putFloat(p.getLocation().y);
        out.putInt(entities.size());
        for(ExpiEntity e : entities) {
            out.putEntity(e);
        }
    }

    public void putChunkFeedPacket(ExpiTile[][] worldTerrain, int c) {
        out.startPacket(13);
        out.putInt(c);

        int totalFixtures = 0;

        int off = c * Constants.T_W_CH;
        for(int i = 0; i < Constants.T_H_CH; i++) {
            for(int j = 0; j < Constants.T_W_CH; j++) {
                ExpiTile t = worldTerrain[i][j+off];
                out.putByte((byte) t.getType().getID());
                out.putByte((byte) t.getStability());
                totalFixtures += t.getFixtures().size();
            }
        }

        Vector2 v1 = new Vector2();
        Vector2 v2 = new Vector2();

        out.putInt(totalFixtures);
        for(int i = 0; i < Constants.T_H_CH; i++) {
            for(int j = 0; j < Constants.T_W_CH; j++) {
                ExpiTile t = worldTerrain[i][j+off];
                for(Fixture f : t.getFixtures()) {
                    EdgeShape shape = (EdgeShape) f.getShape();
                    shape.getVertex1(v1);
                    shape.getVertex2(v2);
                    int fixID = GameServer.get().getWorld().getFixturesID().get(f);
                    out.putFixture(fixID, v1.x, v1.y, v2.x, v2.y);
                }
            }
        }
    }

    public void putChunkDestroyPacket(ExpiTile[][] worldTerrain, int c) {
        out.startPacket(12);
        out.putInt(c);
        List<Integer> IDs = new ArrayList<>();
        int off = c * Constants.T_W_CH;
        for(int i = 0; i < Constants.T_H_CH; i++) {
            for(int j = 0; j < Constants.T_W_CH; j++) {
                ExpiTile t = worldTerrain[i][j + off];
                for(Fixture f : t.getFixtures()) {
                    IDs.add(GameServer.get().getWorld().getFixturesID().get(f));
                }
            }
        }
        out.putInt(IDs.size());
        for(int e : IDs) {
            out.putInt(e);
        }
    }

    public void putEntityMovePacket(ExpiEntity e) {
        out.startPacket(19);
        out.putInt(e.getID());
        out.putFloat(e.getLocation().x);
        out.putFloat(e.getLocation().y);
        out.putFloat(e.getVelocity().x);
        out.putFloat(e.getVelocity().y);
        out.putFloat(e.getBody().getAngle());
        out.putFloat(e.getBody().getAngularVelocity());
    }

    public void putEntitySpawnPacket(ExpiEntity e) {
        out.startPacket(20);
        out.putEntity(e);
    }

    public void putEntityDespawnPacket(ExpiEntity e) {
        out.startPacket(21);
        out.putInt(e.getID());
    }

    public void putInvFeedPacket(ExpiInventory inv) {
        out.startPacket(24);
        out.putInt(inv.getId());
        out.putInt(inv.getItems().size());
        for(ItemStack is : inv.getItems()) {
            out.putInt(is.getItem().getId());
            out.putInt(is.getAmount());
            out.putIntVector(is.getGridPos());
        }
    }

    public void putInvMoveAckPacket(int id, IntVector2 pos1, IntVector2 pos2) {
        out.startPacket(26);
        out.putInt(id);
        out.putIntVector(pos1);
        out.putIntVector(pos2);
    }

    public void putTileBreakAckPacket(List<ExpiTile> brokenTiles, FixturePack fp, HashSet<ExpiTile> at) {
        out.startPacket(17);
        out.putInt(brokenTiles.size());
        for(ExpiTile t : brokenTiles) {
            out.putInt(t.getX() / Constants.T_W_CH);
            out.putInt(t.getX() - (t.getX() / Constants.T_W_CH)*Constants.T_W_CH);
            out.putInt(t.getY());
        }

        Vector2 v1 = new Vector2();
        Vector2 v2 = new Vector2();

        out.putInt(fp.addedFixtures.size());
        for(Fixture f : fp.addedFixtures) {
            EdgeShape shape = (EdgeShape) f.getShape();
            shape.getVertex1(v1);
            shape.getVertex2(v2);
            int fixID = GameServer.get().getWorld().getFixturesID().get(f);
            out.putFixture(fixID, v1.x, v1.y, v2.x, v2.y);
        }
        out.putInt(fp.removedFixtures.size());
        for(int i : fp.removedFixtures) {
            out.putInt(i);
        }
        out.putInt(at.size());
        for(ExpiTile t : at) {
            out.putInt(t.getX() / Constants.T_W_CH);
            out.putInt(t.getX() - (t.getX() / Constants.T_W_CH)*Constants.T_W_CH);
            out.putInt(t.getY());
            out.putInt(t.getStability());
        }
    }

    public void putTilePlaceAckPacket(ExpiTile t, FixturePack fp, HashSet<ExpiTile> at) {
        out.startPacket(18);
        out.putInt(t.getType().getID());
        out.putInt(t.getX() / Constants.T_W_CH);
        out.putInt(t.getX());
        out.putInt(t.getY());

        out.putInt(at.size());
        for(ExpiTile t2 : at) {
            out.putInt(t2.getX() / Constants.T_W_CH);
            out.putInt(t2.getX());
            out.putInt(t2.getY());
            out.putInt(t2.getStability());
        }

        Vector2 v1 = new Vector2();
        Vector2 v2 = new Vector2();

        out.putInt(fp.addedFixtures.size());
        for(Fixture f : fp.addedFixtures) {
            EdgeShape shape = (EdgeShape) f.getShape();
            shape.getVertex1(v1);
            shape.getVertex2(v2);
            int fixID = GameServer.get().getWorld().getFixturesID().get(f);
            out.putFixture(fixID, v1.x, v1.y, v2.x, v2.y);
        }
        out.putInt(fp.removedFixtures.size());
        for(int i : fp.removedFixtures) {
            out.putInt(i);
        }
    }

}
