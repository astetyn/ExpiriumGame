package com.astetyne.expirium.server.net;

import com.astetyne.expirium.client.data.ExtraCell;
import com.astetyne.expirium.client.data.InvVariableType;
import com.astetyne.expirium.client.items.GridItemStack;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.items.ItemRecipe;
import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.ExpiColor;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.entity.Entity;
import com.astetyne.expirium.server.core.entity.player.Player;
import com.astetyne.expirium.server.core.world.WeatherType;
import com.astetyne.expirium.server.core.world.inventory.ChosenSlot;
import com.astetyne.expirium.server.core.world.inventory.Inventory;
import com.astetyne.expirium.server.core.world.inventory.UIInteractType;
import com.astetyne.expirium.server.core.world.tile.Tile;
import com.badlogic.gdx.math.Vector2;

import java.util.HashSet;
import java.util.List;

public class ServerPacketManager {

    private final ExpiServer server;
    private final PacketInputStream in;
    private final PacketOutputStream out;
    private final Player owner;

    public ServerPacketManager(ExpiServer server, Player owner) {
        this.server = server;
        this.owner = owner;
        this.in = owner.getGateway().getIn();
        this.out = owner.getGateway().getOut();
    }

    public void processIncomingPackets() {

        //System.out.println("Server avail packets: "+in.getAvailablePackets());

        for(int i = 0; i < in.getAvailablePackets(); i++) {

            short packetID = in.getShort();
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
                    owner.getInv().onInteract(UIInteractType.get(in.getInt()));
                    break;
            }
        }

    }

    public void putInitDataPacket(List<Entity> entities) {
        out.startPacket(11);

        out.putInt(server.getWorld().getTerrainWidth());
        out.putInt(server.getWorld().getTerrainHeight());
        out.putByte(server.getWorld().getWeather().ordinal());

        out.putShort(owner.getId());
        out.putVector(owner.getLocation());
        out.putInt(entities.size()-1);
        for(Entity e : entities) {
            if(owner == e) continue;
            out.putEntity(e);
        }
    }

    public void putWorldFeedPacket(int layer) {
        out.startPacket(13);

        out.putInt(layer);
        for(int i = 0; i < server.getWorld().getTerrainWidth(); i++) {
            Tile t = server.getWorld().getTileAt(i, layer);
            out.putByte(t.getMaterial().ordinal());
            out.putByte(t.getStability());
            out.putBoolean(t.hasBackWall());
            out.putByte(t.getWaterLevel());
        }
    }

    public void putEntityMovePacket(Entity e) {
        if(!owner.getNearActiveEntities().contains(e) && e != owner) return;
        out.startPacket(19);
        out.putShort(e.getId());
        out.putFloat(e.getLocation().x);
        out.putFloat(e.getLocation().y);
        out.putFloat(e.getBody().getAngle());
        out.putBoolean(e.isLookingRight());
    }

    public void putEntitySpawnPacket(Entity e) {
        out.startPacket(20);
        out.putEntity(e);
    }

    public void putEntityDespawnPacket(Entity e) {
        out.startPacket(21);
        out.putShort(e.getId());
    }

    public void putInvFeedPacket() {
        out.startPacket(24);
        putInvData(owner.getInv());
        putInvData(owner.getSecondInv());
        owner.getSecondInv().writeVariablesData(out);
    }

    private void putInvData(Inventory inv) {
        out.putFloat(inv.getTotalWeight());
        out.putFloat(inv.getMaxWeight());
        out.putByte(inv.getItems().size());
        for(GridItemStack is : inv.getItems()) {
            out.putShort((short) is.getItem().ordinal());
            out.putShort((short) is.getAmount());
            out.putByte(is.getGridPos().x);
            out.putByte(is.getGridPos().y);
        }
    }

    public void putOpenDoubleInvPacket() {
        out.startPacket(31);
        Inventory secondInv = owner.getSecondInv();
        out.putByte(secondInv.getRows());
        out.putByte(secondInv.getColumns());
        out.putString(secondInv.getLabel());
        out.putByte(secondInv.getExtraCells().length);
        for(ExtraCell cell : secondInv.getExtraCells()) {
            out.putByte(secondInv.getIndex(cell.pos));
            out.putByte(cell.tex.ordinal());
        }
        out.putByte(secondInv.getVariables().length);
        for(InvVariableType type : secondInv.getVariables()) {
            out.putByte(type.ordinal());
        }
    }

    public void putHotSlotsFeedPacket(ChosenSlot slot, ItemStack toolIS, ItemStack materialIS, ItemStack consIS) {
        out.startPacket(30);
        out.putByte(slot.ordinal());
        out.putInt(toolIS.getItem().ordinal());
        out.putInt(toolIS.getAmount());
        out.putInt(materialIS.getItem().ordinal());
        out.putInt(materialIS.getAmount());
        out.putInt(consIS.getItem().ordinal());
        out.putInt(consIS.getAmount());
    }

    public void putMaterialChangePacket(Tile t) {
        out.startPacket(22);
        out.putInt(t.getMaterial().ordinal());
        out.putInt(t.getX());
        out.putInt(t.getY());
    }

    public void putStabilityPacket(HashSet<Tile> affectedTiles) {
        out.startPacket(18);
        out.putInt(affectedTiles.size());
        for(Tile t : affectedTiles) {
            out.putInt(t.getX());
            out.putInt(t.getY());
            out.putByte(t.getStability());
        }
    }

    public void putTimePacket() {
        out.startPacket(28);
        out.putInt(server.getWorld().getTime());
    }

    public void putBreakingTilePacket(Tile t, float state) {
        out.startPacket(15);
        out.putInt(t.getX());
        out.putInt(t.getY());
        out.putFloat(state);
    }

    public void putLivingStatsPacket() {
        out.startPacket(27);
        owner.writeLivingStats(out);
    }

    public void putSimpleServerPacket(SimpleServerPacket p) {
        out.startPacket(17);
        out.putInt(p.ordinal());
    }

    public void putDeathPacket(boolean firstDeath, long daysSurvived) {
        out.startPacket(12);
        out.putBoolean(firstDeath);
        out.putLong(daysSurvived);
    }

    public void putHandPunchPacket(Player puncher) {
        out.startPacket(32);
        out.putShort(puncher.getId());
    }

    public void putHandItemPacket(short id, Item item) {
        out.startPacket(33);
        out.putShort(id);
        out.putInt(item.ordinal());
    }

    public void putBackWallPacket(List<Tile> backWallTiles) {
        out.startPacket(34);
        out.putInt(backWallTiles.size());
        for(Tile t : backWallTiles) {
            out.putInt(t.getX());
            out.putInt(t.getY());
            out.putBoolean(t.hasBackWall());
        }
    }

    public void putWaterPacket(HashSet<Tile> updatedTiles) {
        out.startPacket(36);
        out.putInt(updatedTiles.size());
        for(Tile t : updatedTiles) {
            out.putInt(t.getX());
            out.putInt(t.getY());
            out.putByte(t.getWaterLevel());
        }
    }

    public void putPlayTextAnim(Vector2 loc, String text, ExpiColor c) {
        if(owner.getCenter().dst(loc) > Consts.ACTIVE_ENTITIES_RADIUS) return;
        out.startPacket(35);
        out.putVector(loc);
        out.putString(text);
        out.putColor(c);
    }

    public void putWarningPacket(String msg, int durationMillis, ExpiColor c) {
        out.startPacket(23);
        out.putString(msg);
        out.putInt(durationMillis);
        out.putColor(c);
    }

    public void putWeatherChangePacket(WeatherType newWeather) {
        out.startPacket(37);
        out.putByte(newWeather.ordinal());
    }
}
