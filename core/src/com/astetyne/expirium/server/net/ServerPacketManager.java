package com.astetyne.expirium.server.net;

import com.astetyne.expirium.client.items.GridItemStack;
import com.astetyne.expirium.client.items.ItemRecipe;
import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.client.world.input.InteractType;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.api.entity.ExpiEntity;
import com.astetyne.expirium.server.api.entity.ExpiPlayer;
import com.astetyne.expirium.server.api.event.PlayerInteractEvent;
import com.astetyne.expirium.server.api.event.PlayerInteractListener;
import com.astetyne.expirium.server.api.world.inventory.ExpiInventory;
import com.astetyne.expirium.server.api.world.inventory.UIInteractType;
import com.astetyne.expirium.server.api.world.tiles.ExpiTile;

import java.util.HashSet;
import java.util.List;

public class ServerPacketManager {

    private final PacketInputStream in;
    private final PacketOutputStream out;
    private final ExpiPlayer owner;

    public ServerPacketManager(ExpiPlayer owner) {
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
                    float x = in.getFloat();
                    float y = in.getFloat();
                    InteractType type = InteractType.getType(in.getInt());
                    ExpiTile tile = ExpiServer.get().getWorld().getTileAt(x, y);
                    PlayerInteractEvent e = new PlayerInteractEvent(owner, x, y, tile, type);
                    List<PlayerInteractListener> list = ExpiServer.get().getEventManager().getPlayerInteractListeners();
                    for(int j = list.size() - 1; j >= 0; j--) {
                        list.get(j).onInteract(e);
                    }
                    break;

                case 25: {//InvItemMoveReqPacket
                    owner.onInvMove(in);
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

        out.putInt(owner.getID());
        out.putVector(owner.getLocation());
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
                out.putByte((byte) t.getTypeFront().getID());
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

    public void putInvFeedPacket() {
        ExpiInventory inv1 = owner.getInv();
        ExpiInventory inv2 = owner.getSecondInv();
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
        out.putInt(owner.getSecondInv().getGrid().length);
        out.putInt(owner.getSecondInv().getGrid()[0].length);
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
        out.putInt(t.getTypeFront().getID());
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
        out.putFloat(ExpiServer.get().getWorld().getDayTime());
        out.putInt(ExpiServer.get().getWorld().getWeather().getID());
    }

    public void putBreakingTilePacket(ExpiTile t, float state) {
        out.startPacket(15);
        out.putInt(t.getX());
        out.putInt(t.getY());
        out.putFloat(state);
    }

    public void putLivingStatsPacket() {
        out.startPacket(27);
        out.putFloat(owner.getHealthLevel());
        out.putFloat(owner.getFoodLevel());
        out.putFloat(owner.getTemperatureLevel());
    }

    public void putSimpleServerPacket(SimpleServerPacket p) {
        out.startPacket(17);
        out.putInt(p.getID());
    }
}
