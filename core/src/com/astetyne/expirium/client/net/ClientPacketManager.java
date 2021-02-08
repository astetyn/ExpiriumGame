package com.astetyne.expirium.client.net;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.GameInfo;
import com.astetyne.expirium.client.data.ThumbStickData;
import com.astetyne.expirium.client.entity.Entity;
import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.client.entity.Player;
import com.astetyne.expirium.client.gui.roots.game.DeathRoot;
import com.astetyne.expirium.client.gui.roots.game.DoubleInventoryRoot;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.items.ItemRecipe;
import com.astetyne.expirium.client.screens.GameScreen;
import com.astetyne.expirium.client.utils.IntVector2;
import com.astetyne.expirium.client.world.GameWorld;
import com.astetyne.expirium.client.world.input.InteractType;
import com.astetyne.expirium.server.core.world.WeatherType;
import com.astetyne.expirium.server.core.world.inventory.UIInteractType;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.astetyne.expirium.server.net.PacketOutputStream;
import com.astetyne.expirium.server.net.SimpleServerPacket;

public class ClientPacketManager {

    private final PacketInputStream in;
    private final PacketOutputStream out;

    public ClientPacketManager(PacketInputStream in, PacketOutputStream out) {
        this.in = in;
        this.out = out;
    }

    public void processIncomingPackets() {

        GameWorld world = null;
        if(GameScreen.get() != null) {
            world = GameScreen.get().getWorld();
            world.onServerTick();
        }

        int availPackets = in.getAvailablePackets();

        //System.out.println("Coming avail packets: "+availPackets);

        for(int i = 0; i < availPackets; i++) {

            int packetID = in.getInt();

            //System.out.println("C: PID: " + packetID);

            switch(packetID) {

                case 11:
                    ExpiGame.get().setScreen(new GameScreen(in));
                    world = GameScreen.get().getWorld();
                    break;
                case 12: // DeathPacket
                    boolean firstDeath = in.getBoolean();
                    long daysSurvived = in.getLong();
                    GameScreen.get().setRoot(new DeathRoot(firstDeath, daysSurvived));
                    break;

                case 13: //WorldFeedPacket
                    world.onFeedWorldEvent(in);
                    break;

                case 15: //BreakingTilePacket
                    world.onBreakingTile(in);
                    break;

                case 17: //SimpleServerPacket
                    GameScreen.get().onSimplePacket(SimpleServerPacket.getType(in.getInt()));
                    break;
                case 18: //StabilityPacket
                    world.onStabilityChange(in);
                    break;

                case 19: {//EntityMovePacket
                    int eID = in.getInt();
                    Entity e = world.getEntitiesID().get(eID);
                    if(e != null) {
                        e.onMove(in);
                    }else {
                        in.skip(6 * 4);
                    }
                    break;
                }
                case 20: //EntitySpawnPacket
                    EntityType.getType(in.getInt()).initEntity(in);
                    break;

                case 21: //EntityDespawnPacket
                    int id = in.getInt();
                    world.getEntitiesID().get(id).destroy();
                    break;

                case 22: //TileChangePacket
                    world.onTileChange(in);
                    break;

                case 23: {//InjurePacket
                    Entity e = world.getEntitiesID().get(in.getInt());
                    e.injure(in.getFloat());
                    break;
                }
                case 24: //InvFeedPacket
                    GameScreen.get().getPlayerData().feedInventory(in);
                    break;

                case 27: //living stats
                    GameScreen.get().getPlayerData().feedLivingStats(in);
                    break;

                case 28: //EnviroPacket
                    GameScreen.get().setTime(in.getInt());
                    WeatherType weather = WeatherType.getType(in.getInt());
                    break;

                case 30: //InvHotSlotsFeedPacket
                    GameScreen.get().getPlayerData().getHotSlotsData().feed(in);
                    GameScreen.get().getActiveRoot().refresh();
                    break;
                case 31: //OpenDoubleInvPacket
                    GameScreen.get().setRoot(new DoubleInventoryRoot(in));
                    break;

                case 32: {//HandPunchPacket
                    Player p = (Player) world.getEntitiesID().get(in.getInt());
                    p.onHandPunch();
                    break;
                }
                case 33: { //HandItemPacket
                    Player p = (Player) world.getEntitiesID().get(in.getInt());
                    p.setItemInHand(Item.getType(in.getInt()));
                    break;
                }
                case 34: // BackWallPacket
                    world.onBackWallsChange(in);
                    break;
            }
        }
    }

    public void putJoinReqPacket(String name) {
        out.startPacket(10);
        out.putString(name);
    }

    public void putTSPacket() {
        ThumbStickData data1 = GameScreen.get().getPlayerData().getThumbStickData1();
        ThumbStickData data2 = GameScreen.get().getPlayerData().getThumbStickData2();

        out.startPacket(14);
        out.putFloat(data1.horz);
        out.putFloat(data1.vert);
        out.putFloat(data2.horz);
        out.putFloat(data2.vert);
    }

    public void putInteractPacket(float x, float y, InteractType type) {
        out.startPacket(16);
        out.putFloat(x);
        out.putFloat(y);
        out.putInt(type.getID());
    }

    public void putUIInteractPacket(UIInteractType action) {
        out.startPacket(29);
        out.putInt(action.getID());
    }

    public void putInvItemMoveReqPacket(boolean fromMain, IntVector2 pos1, boolean toMain, IntVector2 pos2) {
        out.startPacket(25);
        out.putBoolean(fromMain);
        out.putIntVector(pos1);
        out.putBoolean(toMain);
        out.putIntVector(pos2);
    }

    public void putInvItemMakeReqPacket(ItemRecipe recipe) {
        out.startPacket(26);
        out.putInt(recipe.getId());
    }

    public void putGameInfoPacket(GameInfo gameInfo) {
        out.startPacket(12);
        out.putInt(gameInfo.getID());
    }
}
