package com.astetyne.expirium.server.backend;

import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.entities.ExpiEntity;
import com.astetyne.expirium.server.api.entities.ExpiPlayer;
import com.astetyne.expirium.server.api.world.ExpiTile;
import com.astetyne.expirium.server.api.world.inventory.ExpiInventory;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;

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

        FixturePack fp = new FixturePack();

        int off = c * Consts.T_W_CH;
        for(int i = 0; i < Consts.T_H_CH; i++) {
            for(int j = 0; j < Consts.T_W_CH; j++) {
                ExpiTile t = worldTerrain[i][j+off];
                out.putByte((byte) t.getType().getID());
                out.putByte((byte) t.getStability());
                fp.addedFixtures.addAll(t.getFixtures());
            }
        }
        putFixturePacket(fp);
    }

    public void putChunkDestroyPacket(ExpiTile[][] worldTerrain, int c) {
        out.startPacket(12);
        out.putInt(c);

        FixturePack fp = new FixturePack();
        int off = c * Consts.T_W_CH;
        for(int i = 0; i < Consts.T_H_CH; i++) {
            for(int j = 0; j < Consts.T_W_CH; j++) {
                ExpiTile t = worldTerrain[i][j + off];
                for(Fixture f : t.getFixtures()) {
                    fp.removedFixtures.add(GameServer.get().getWorld().getFixturesID().get(f));
                }
            }
        }
        putFixturePacket(fp);
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
        out.putFloat(inv.getTotalWeight());
        out.putFloat(inv.getMaxWeight());
        out.putInt(inv.getItems().size());
        for(ItemStack is : inv.getItems()) {
            out.putInt(is.getItem().getId());
            out.putInt(is.getAmount());
            out.putIntVector(is.getGridPos());
        }
    }

    public void putHotSlotsFeedPacket(ItemStack toolIS, ItemStack materialIS, ItemStack consIS) {
        out.startPacket(30);
        out.putInt(toolIS.getItem().getId());
        out.putInt(toolIS.getAmount());
        out.putInt(materialIS.getItem().getId());
        out.putInt(materialIS.getAmount());
        out.putInt(consIS.getItem().getId());
        out.putInt(consIS.getAmount());
    }

    public void putTileChangePacket(ExpiTile t) {
        out.startPacket(22);
        out.putInt(t.getType().getID());
        out.putInt(t.getX() / Consts.T_W_CH);
        out.putInt(t.getX() - (t.getX() / Consts.T_W_CH)* Consts.T_W_CH);
        out.putInt(t.getY());
    }

    public void putFixturePacket(FixturePack pack) {
        out.startPacket(17);

        Vector2 v1 = new Vector2();
        Vector2 v2 = new Vector2();

        out.putInt(pack.addedFixtures.size());
        for(Fixture f : pack.addedFixtures) {
            EdgeShape shape = (EdgeShape) f.getShape();
            shape.getVertex1(v1);
            shape.getVertex2(v2);
            int fixID = GameServer.get().getWorld().getFixturesID().get(f);
            out.putFixture(fixID, v1.x, v1.y, v2.x, v2.y);
        }
        out.putInt(pack.removedFixtures.size());
        for(int i : pack.removedFixtures) {
            out.putInt(i);
        }
    }

    public void putStabilityPacket(HashSet<ExpiTile> affectedTiles) {
        out.startPacket(18);
        out.putInt(affectedTiles.size());
        for(ExpiTile t : affectedTiles) {
            out.putInt(t.getX() / Consts.T_W_CH);
            out.putInt(t.getX() - (t.getX() / Consts.T_W_CH)* Consts.T_W_CH);
            out.putInt(t.getY());
            out.putInt(t.getStability());
        }
    }

    public void putEnviroPacket() {
        out.startPacket(28);
        out.putInt(GameServer.get().getServerTime());
        out.putInt(GameServer.get().getWorld().getWeather().getID());
    }
}
