package com.astetyne.expirium.client.net;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.GameInfo;
import com.astetyne.expirium.client.data.ThumbStickData;
import com.astetyne.expirium.client.entity.ClientEntity;
import com.astetyne.expirium.client.entity.ClientPlayer;
import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.client.gui.roots.game.DeathRoot;
import com.astetyne.expirium.client.gui.roots.game.DoubleInventoryRoot;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.items.ItemRecipe;
import com.astetyne.expirium.client.resources.PlayerCharacter;
import com.astetyne.expirium.client.screens.GameScreen;
import com.astetyne.expirium.client.utils.IntVector2;
import com.astetyne.expirium.client.world.input.InteractType;
import com.astetyne.expirium.server.core.world.WeatherType;
import com.astetyne.expirium.server.core.world.inventory.UIInteractType;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.astetyne.expirium.server.net.PacketOutputStream;
import com.astetyne.expirium.server.net.SimpleServerPacket;

public class ClientPacketManager {

    private final PacketInputStream in;
    private final PacketOutputStream out;
    private GameScreen game;

    public ClientPacketManager(PacketInputStream in, PacketOutputStream out) {
        this.in = in;
        this.out = out;
    }

    public void processIncomingPackets() {

        if(game != null) {
            game.getWorld().onServerTick();
        }

        int availPackets = in.getAvailablePackets();

        //System.out.println("Coming avail packets: "+availPackets);

        for(int i = 0; i < availPackets; i++) {

            short packetID = in.getShort();

            //System.out.println("C: PID: " + packetID);

            switch(packetID) {

                case 11:
                    game = new GameScreen(in);
                    ExpiGame.get().setScreen(game);
                    break;
                case 12: // DeathPacket
                    boolean firstDeath = in.getBoolean();
                    long daysSurvived = in.getLong();
                    game.setRoot(new DeathRoot(game, firstDeath, daysSurvived));
                    break;

                case 13: //WorldFeedPacket
                    game.getWorld().onFeedWorldEvent(in);
                    break;

                case 15: //BreakingTilePacket
                    game.getWorld().onBreakingTile(in);
                    break;

                case 17: //SimpleServerPacket
                    game.onSimplePacket(SimpleServerPacket.get(in.getInt()));
                    break;
                case 18: //StabilityPacket
                    game.getWorld().onStabilityChange(in);
                    break;

                case 19: {//EntityMovePacket
                    short eID = in.getShort();
                    ClientEntity e = game.getWorld().getEntitiesID().get(eID);
                    if(e != null) {
                        e.onMove(in);
                    }else {
                        in.skip(6 * 4);
                    }
                    break;
                }
                case 20: //EntitySpawnPacket
                    EntityType.getType(in.getInt()).initEntity(game.getWorld(), in);
                    break;

                case 21: //EntityDespawnPacket
                    short id = in.getShort();
                    game.getWorld().getEntitiesID().get(id).destroy();
                    break;

                case 22: //TileChangePacket
                    game.getWorld().onTileChange(in);
                    break;

                case 23: //WarningMsgPacket
                    game.onWarningMsgPacket(in);
                    break;

                case 24: //InvFeedPacket
                    game.getPlayerData().feedInventory(in);
                    break;

                case 27: //living stats
                    game.getPlayerData().feedLivingStats(in);
                    break;

                case 28: //TimePacket
                    game.onTimePacket(in);
                    break;

                case 30: //InvHotSlotsFeedPacket
                    game.getPlayerData().getHotSlotsData().feed(in);
                    game.getActiveRoot().refresh();
                    break;
                case 31: //OpenDoubleInvPacket
                    game.setRoot(new DoubleInventoryRoot(game, in));
                    break;

                case 32: {//HandPunchPacket
                    ClientPlayer p = (ClientPlayer) game.getWorld().getEntitiesID().get(in.getShort());
                    p.onHandPunch();
                    break;
                }
                case 33: { //HandItemPacket
                    ClientPlayer p = (ClientPlayer) game.getWorld().getEntitiesID().get(in.getShort());
                    p.setItemInHand(Item.get(in.getInt()));
                    break;
                }
                case 34: // BackWallPacket
                    game.getWorld().onBackWallsChange(in);
                    break;

                case 35: // PlayTextAnimation
                    game.getWorld().getAnimationManager().onPlayTextAnimation(in);
                    break;

                case 36: // WaterPacket
                    game.getWorld().onWaterPacket(in);
                    break;

                case 37: // WeatherChangePacket
                    game.setWeather(WeatherType.get(in.getByte()));
                    break;
            }
        }
    }

    public void putJoinReqPacket(String name, PlayerCharacter character) {
        out.startPacket(10);
        out.putString(name);
        out.putByte(character.ordinal());
    }

    public void putTSPacket() {
        if(game == null) return;
        ThumbStickData data1 = game.getPlayerData().getThumbStickData1();
        ThumbStickData data2 = game.getPlayerData().getThumbStickData2();

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
        out.putInt(action.ordinal());
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
