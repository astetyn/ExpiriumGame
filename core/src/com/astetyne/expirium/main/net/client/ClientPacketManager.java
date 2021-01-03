package com.astetyne.expirium.main.net.client;

import com.astetyne.expirium.main.ExpiriumGame;
import com.astetyne.expirium.main.entity.Entity;
import com.astetyne.expirium.main.entity.EntityType;
import com.astetyne.expirium.main.items.Item;
import com.astetyne.expirium.main.items.ItemRecipe;
import com.astetyne.expirium.main.stages.GameStage;
import com.astetyne.expirium.main.utils.IntVector2;
import com.astetyne.expirium.main.world.GameWorld;
import com.astetyne.expirium.main.world.WeatherType;
import com.astetyne.expirium.main.world.input.InteractType;
import com.astetyne.expirium.main.world.tiles.Tile;
import com.astetyne.expirium.server.api.world.inventory.InvInteractType;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.astetyne.expirium.server.backend.PacketOutputStream;

public class ClientPacketManager {

    private final PacketInputStream in;
    private final PacketOutputStream out;

    public ClientPacketManager(PacketInputStream in, PacketOutputStream out) {
        this.in = in;
        this.out = out;
    }

    public void putJoinReqPacket(String name) {
        out.startPacket(10);
        out.putString(name);
    }

    public void putTS1Packet(float horz, float vert) {
        out.startPacket(14);
        out.putFloat(horz);
        out.putFloat(vert);
    }

    public void putTS2Packet(float horz, float vert) {
        out.startPacket(15);
        out.putFloat(horz);
        out.putFloat(vert);
    }

    public void putTileBreakReqPacket(Tile t) {
        out.startPacket(15);
        out.putInt(t.getC());
        out.putInt(t.getX());
        out.putInt(t.getY());
    }

    public void putTilePlaceReqPacket(Tile t, Item placedItem) {
        out.startPacket(16);
        out.putInt(t.getC());
        out.putInt(t.getX());
        out.putInt(t.getY());
        out.putInt(placedItem.getId());
    }

    public void putInteractPacket(float x, float y, InteractType type) {
        out.startPacket(16);
        out.putFloat(x);
        out.putFloat(y);
        out.putInt(type.getID());
    }

    public void putInvInteractPacket(InvInteractType action) {
        out.startPacket(29);
        out.putInt(action.getID());
    }

    public void putInvOpenReqPacket(int id) {
        out.startPacket(23);
        out.putInt(id);
    }

    public void putInvItemMoveReqPacket(int id, IntVector2 pos1, int id2, IntVector2 pos2) {
        out.startPacket(25);
        out.putInt(id);
        out.putIntVector(pos1);
        out.putInt(id2);
        out.putIntVector(pos2);
    }

    public void putInvItemMakeReqPacket(ItemRecipe recipe) {
        out.startPacket(26);
        out.putInt(recipe.getId());
    }

    public void processIncomingPackets() {

        GameWorld world = null;
        if(ExpiriumGame.get().getCurrentStage() instanceof GameStage) {
            world = GameStage.get().getWorld();
        }

        int availPackets = in.getAvailablePackets();

        for(int i = 0; i < availPackets; i++) {

            int packetID = in.getInt();

            //System.out.println("C: PID: " + packetID);

            switch(packetID) {

                case 11:
                    GameStage game = new GameStage();
                    game.initGameStage();
                    ExpiriumGame.get().setCurrentStage(game);
                    world = GameStage.get().getWorld();
                    break;
                case 12: //ChunkDestroyPacket
                    world.onDestroyChunkEvent();
                    break;

                case 13: //ChunkFeedPacket
                    world.onFeedChunkEvent();
                    break;

                case 17: //FixturePacket
                    world.onFixturesChange(in);
                    break;

                case 18: //StabilityPacket
                    world.onStabilityChange(in);
                    break;

                case 19: //EntityMovePacket
                    int eID = in.getInt();
                    Entity e = world.getEntitiesID().get(eID);
                    if(e != null) {
                        e.onMove(in);
                    }else {
                        in.skip(6 * 4);
                    }
                    break;

                case 20: //EntitySpawnPacket
                    EntityType.getType(in.getInt()).initEntity();
                    break;

                case 21: //EntityDespawnPacket
                    int id = in.getInt();
                    world.getEntitiesID().get(id).destroy();
                    break;

                case 22: //TileChangePacket
                    world.onTileChange(in);
                    break;

                case 24: //InvFeedPacket
                    GameStage.get().getInv().getStorageGridIDs().get(in.getInt()).onInvFeed(in);
                    break;

                case 28: //EnviroPacket
                    GameStage.get().setServerTime(in.getInt());
                    WeatherType weather = WeatherType.getType(in.getInt());
                    break;

                case 30: //InvHotSlotsFeedPacket
                    GameStage.get().getInv().feedHotSlots(in);
                    break;
            }
        }
    }
}
