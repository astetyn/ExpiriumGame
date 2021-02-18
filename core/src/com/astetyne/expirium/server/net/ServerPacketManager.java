package com.astetyne.expirium.server.net;

import com.astetyne.expirium.client.items.GridItemStack;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.items.ItemRecipe;
import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.client.utils.ExpiColor;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.entity.ExpiEntity;
import com.astetyne.expirium.server.core.entity.player.ExpiPlayer;
import com.astetyne.expirium.server.core.world.inventory.ChosenSlot;
import com.astetyne.expirium.server.core.world.inventory.Inventory;
import com.astetyne.expirium.server.core.world.inventory.UIInteractType;
import com.astetyne.expirium.server.core.world.tile.ExpiTile;
import com.badlogic.gdx.math.Vector2;

import java.util.HashSet;
import java.util.List;

public class ServerPacketManager {

    private final ExpiServer server;
    private final PacketInputStream in;
    private final PacketOutputStream out;
    private final ExpiPlayer owner;

    public ServerPacketManager(ExpiServer server, ExpiPlayer owner) {
        this.server = server;
        this.owner = owner;
        this.in = owner.getGateway().getIn();
        this.out = owner.getGateway().getOut();
    }

    public void processIncomingPackets() {

        //System.out.println("Server avail packets: "+in.getAvailablePackets());

        for(int i = 0; i < in.getAvailablePackets(); i++) {

            int packetID = in.getInt();
            //System.out.println("S: PID: " + packetID);

            switch(packetID) {

                case 14: //TSPacket
                    owner.updateThumbSticks(in);
                    break;

                case 16: //InteractPacket
                    server.getWorld().onInteract(owner, in);
                    break;

                case 25: {//InvItemMoveReqPacket
                    owner.getInv().onMove(owner.getSecondInv(), in);
                    break;
                }
                case 26: {//InvItemMakeReqPacket
                    ItemRecipe recipe = ItemRecipe.getRecipe(in.getInt());
                    owner.wantsToMakeItem(recipe);
                    break;
                }
                case 29://UIInteractPacket
                    owner.getInv().onInteract(UIInteractType.getType(in.getInt()));
                    break;
            }
        }

    }

    public void putInitDataPacket(ExpiTile[][] terrain, List<ExpiEntity> entities) {
        out.startPacket(11);

        int w = terrain[0].length;
        int h = terrain.length;

        out.putInt(w);
        out.putInt(h);

        out.putInt(owner.getId());
        out.putVector(owner.getLocation());
        out.putInt(entities.size()-1);
        for(ExpiEntity e : entities) {
            if(owner == e) continue;
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
                out.putByte((byte) t.getMaterial().getID());
                out.putByte((byte) t.getStability());
                out.putBoolean(t.hasBackWall());
            }
        }
    }

    public void putEntityMovePacket(ExpiEntity e) {
        //todo: optimalizacia, pozriet sa, ci to nie je moc daleko
        out.startPacket(19);
        out.putInt(e.getId());
        out.putFloat(e.getLocation().x);
        out.putFloat(e.getLocation().y);
        out.putFloat(e.getVelocity().x);
        out.putFloat(e.getVelocity().y);
        out.putFloat(e.getBody().getAngle());
        out.putBoolean(e.isLookingRight());
    }

    public void putEntitySpawnPacket(ExpiEntity e) {
        out.startPacket(20);
        out.putEntity(e);
    }

    public void putEntityDespawnPacket(ExpiEntity e) {
        out.startPacket(21);
        out.putInt(e.getId());
    }

    public void putInvFeedPacket() {
        Inventory inv1 = owner.getInv();
        Inventory inv2 = owner.getSecondInv();
        out.startPacket(24);
        out.putString(inv1.getLabel());
        out.putFloat(inv1.getTotalWeight());
        out.putFloat(inv1.getMaxWeight());
        out.putInt(inv1.getItems().size());
        for(GridItemStack is : inv1.getItems()) {
            out.putInt(is.getItem().getId());
            out.putInt(is.getAmount());
            out.putIntVector(is.getGridPos());
        }
        out.putString(inv2.getLabel());
        out.putFloat(inv2.getTotalWeight());
        out.putFloat(inv2.getMaxWeight());
        out.putInt(inv2.getItems().size());
        for(GridItemStack is : inv2.getItems()) {
            out.putInt(is.getItem().getId());
            out.putInt(is.getAmount());
            out.putIntVector(is.getGridPos());
        }
    }

    public void putOpenDoubleInvPacket() {
        out.startPacket(31);
        out.putInt(owner.getSecondInv().getRows());
        out.putInt(owner.getSecondInv().getColumns());
    }

    public void putHotSlotsFeedPacket(ChosenSlot slot, ItemStack toolIS, ItemStack materialIS, ItemStack consIS) {
        out.startPacket(30);
        out.putByte((byte) slot.getId());
        out.putInt(toolIS.getItem().getId());
        out.putInt(toolIS.getAmount());
        out.putInt(materialIS.getItem().getId());
        out.putInt(materialIS.getAmount());
        out.putInt(consIS.getItem().getId());
        out.putInt(consIS.getAmount());
    }

    public void putMaterialChangePacket(ExpiTile t) {
        out.startPacket(22);
        out.putInt(t.getMaterial().getID());
        out.putInt(t.getX());
        out.putInt(t.getY());
    }

    public void putStabilityPacket(HashSet<ExpiTile> affectedTiles) {
        out.startPacket(18);
        out.putInt(affectedTiles.size());
        for(ExpiTile t : affectedTiles) {
            out.putInt(t.getX());
            out.putInt(t.getY());
            out.putByte((byte) t.getStability());
        }
    }

    public void putEnviroPacket() {
        out.startPacket(28);
        out.putInt(server.getWorld().getTime());
        out.putInt(server.getWorld().getWeather().getID());
    }

    public void putBreakingTilePacket(ExpiTile t, float state) {
        out.startPacket(15);
        out.putInt(t.getX());
        out.putInt(t.getY());
        out.putFloat(state);
    }

    public void putLivingStatsPacket() {
        out.startPacket(27);
        out.putByte(owner.getHealthLevel());
        out.putByte(owner.getFoodLevel());
    }

    public void putSimpleServerPacket(SimpleServerPacket p) {
        out.startPacket(17);
        out.putInt(p.getID());
    }

    public void putDeathPacket(boolean firstDeath, long daysSurvived) {
        out.startPacket(12);
        out.putBoolean(firstDeath);
        out.putLong(daysSurvived);
    }

    public void putHandPunchPacket(ExpiPlayer puncher) {
        out.startPacket(32);
        out.putInt(puncher.getId());
    }

    public void putHandItemPacket(int id, Item item) {
        out.startPacket(33);
        out.putInt(id);
        out.putInt(item.getId());
    }

    public void putBackWallPacket(List<ExpiTile> backWallTiles) {
        out.startPacket(34);
        out.putInt(backWallTiles.size());
        for(ExpiTile t : backWallTiles) {
            out.putInt(t.getX());
            out.putInt(t.getY());
            out.putBoolean(t.hasBackWall());
        }
    }

    public void putPlayTextAnim(Vector2 loc, String text, ExpiColor c) {
        //todo: optimalizacia, pozriet sa, ci to nie je moc daleko
        out.startPacket(35);
        out.putVector(loc);
        out.putString(text);
        out.putByte(c.getId());
    }
}
