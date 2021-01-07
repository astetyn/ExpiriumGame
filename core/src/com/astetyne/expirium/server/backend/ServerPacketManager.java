package com.astetyne.expirium.server.backend;

import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.entities.ExpiEntity;
import com.astetyne.expirium.server.api.entities.ExpiPlayer;
import com.astetyne.expirium.server.api.world.ExpiTile;
import com.astetyne.expirium.server.api.world.inventory.ExpiInventory;

import java.util.HashSet;
import java.util.List;

public class ServerPacketManager {

    private final PacketInputStream in;
    private final PacketOutputStream out;

    public ServerPacketManager(PacketInputStream in, PacketOutputStream out) {
        this.in = in;
        this.out = out;
    }

    public void putInitDataPacket(ExpiTile[][] terrain, ExpiPlayer p, List<ExpiEntity> entities) {
        out.startPacket(11);

        int w = terrain[0].length;
        int h = terrain.length;

        out.putInt(w);
        out.putInt(h);

        out.putInt(p.getID());
        out.putVector(p.getLocation());
        out.putInt(entities.size());
        for(ExpiEntity e : entities) {
            out.putEntity(e);
        }
    }

    public void putWorldFeedPacket(ExpiTile[][] terrain, int partHeight, int partNumber) {
        out.startPacket(13);

        out.putInt(partHeight);
        out.putInt(partNumber);

        int yOff = partNumber * partHeight;
        for(int i = 0; i < terrain[0].length; i++) {
            for(int j = yOff; j < yOff + partHeight; j++) {
                ExpiTile t = terrain[j][i];
                out.putByte((byte) t.getType().getID());
                out.putByte((byte) t.getStability());
            }
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
    }

    public void putEntitySpawnPacket(ExpiEntity e) {
        out.startPacket(20);
        out.putEntity(e);
    }

    public void putEntityDespawnPacket(ExpiEntity e) {
        out.startPacket(21);
        out.putInt(e.getID());
    }

    public void putInvFeedPacket(ExpiPlayer p) {
        ExpiInventory inv1 = p.getInv();
        ExpiInventory inv2 = p.getSecondInv();
        out.startPacket(24);
        out.putString(inv1.getLabel());
        out.putFloat(inv1.getTotalWeight());
        out.putFloat(inv1.getMaxWeight());
        out.putInt(inv1.getItems().size());
        for(ItemStack is : inv1.getItems()) {
            out.putInt(is.getItem().getId());
            out.putInt(is.getAmount());
            out.putIntVector(is.getGridPos());
        }
        out.putString(inv2.getLabel());
        out.putFloat(inv2.getTotalWeight());
        out.putFloat(inv2.getMaxWeight());
        out.putInt(inv2.getItems().size());
        for(ItemStack is : inv2.getItems()) {
            out.putInt(is.getItem().getId());
            out.putInt(is.getAmount());
            out.putIntVector(is.getGridPos());
        }
    }

    public void putOpenDoubleInvPacket(ExpiInventory secondInv) {
        out.startPacket(31);
        out.putInt(secondInv.getGrid().length);
        out.putInt(secondInv.getGrid()[0].length);
    }

    public void putHotSlotsFeedPacket(byte focus, ItemStack toolIS, ItemStack materialIS, ItemStack consIS) {
        out.startPacket(30);
        out.putByte(focus);
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
        out.putInt(t.getX());
        out.putInt(t.getY());
    }

    public void putStabilityPacket(HashSet<ExpiTile> affectedTiles) {
        out.startPacket(18);
        out.putInt(affectedTiles.size());
        for(ExpiTile t : affectedTiles) {
            out.putInt(t.getX());
            out.putInt(t.getY());
            out.putInt(t.getStability());
        }
    }

    public void putEnviroPacket() {
        out.startPacket(28);
        out.putInt(GameServer.get().getWorld().getWorldTime());
        out.putInt(GameServer.get().getWorld().getWeather().getID());
    }

    public void putBreakingTile(ExpiTile t, float state) {
        out.startPacket(15);
        out.putInt(t.getX());
        out.putInt(t.getY());
        out.putFloat(state);
    }
}
