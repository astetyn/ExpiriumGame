package com.astetyne.expirium.main.net.client;

import com.astetyne.expirium.main.ExpiriumGame;
import com.astetyne.expirium.main.entity.Entity;
import com.astetyne.expirium.main.entity.EntityType;
import com.astetyne.expirium.main.items.ItemType;
import com.astetyne.expirium.main.stages.GameStage;
import com.astetyne.expirium.main.utils.IntVector2;
import com.astetyne.expirium.main.world.GameWorld;
import com.astetyne.expirium.main.world.tiles.Tile;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.astetyne.expirium.server.backend.PacketOutputStream;
import com.badlogic.gdx.math.Vector2;

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

    public void putPlayerMovePacket(Vector2 loc, Vector2 velocity) {
        out.startPacket(14);
        out.putVector(loc);
        out.putVector(velocity);
    }

    public void putTileBreakReqPacket(Tile t) {
        out.startPacket(15);
        out.putInt(t.getChunk().getId());
        out.putInt(t.getX());
        out.putInt(t.getY());
    }

    public void putTilePlaceReqPacket(Tile t, ItemType placedItem) {
        out.startPacket(16);
        out.putInt(t.getChunk().getId());
        out.putInt(t.getX());
        out.putInt(t.getY());
        out.putInt(placedItem.getId());
    }

    public void putInvOpenReqPacket(int id) {
        out.startPacket(23);
        out.putInt(id);
    }

    public void putInvItemMoveReqPacket(int id, IntVector2 pos1, IntVector2 pos2) {
        out.startPacket(25);
        out.putInt(id);
        out.putIntVector(pos1);
        out.putIntVector(pos2);
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

                case 17: //BreakTileAckPacket
                    world.onBreakTileEvent();
                    break;

                case 18: //BreakPlaceAckPacket
                    world.onPlaceTileEvent();
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

                case 24: //InvFeedPacket
                    GameStage.get().getInv().getStorageGridIDs().get(in.getInt()).onInvFeed(in);
                    break;

                case 26: //InvItemMoveAckPacket
                    GameStage.get().getInv().getStorageGridIDs().get(in.getInt()).onInvItemMove(in);
                    break;
            }
        }
    }
}
