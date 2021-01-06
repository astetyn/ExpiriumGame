package com.astetyne.expirium.server.backend;

import com.astetyne.expirium.main.items.ItemRecipe;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.main.utils.IntVector2;
import com.astetyne.expirium.main.world.input.InteractType;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.entities.ExpiEntity;
import com.astetyne.expirium.server.api.entities.ExpiPlayer;
import com.astetyne.expirium.server.api.world.inventory.InvInteractType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TickLooper extends TerminableLooper {

    private final Object tickLock;
    private final GameServer server;
    private final List<ExpiPlayer> players;

    public TickLooper() {
        tickLock = new Object();
        server = GameServer.get();
        players = server.getPlayers();
    }

    @Override
    public void run() {

        try {

            while(isRunning()) {

                resolveLeavingPlayers();

                resolveJoiningPlayers();

                resolvePlayersActions();

                GameServer.get().onTick();

                // wakes up all clients threads and send new actions
                synchronized(tickLock) {
                    tickLock.notifyAll();
                }

                //noinspection BusyWait
                Thread.sleep(1000 / Consts.SERVER_DEFAULT_TPS);
            }

        }catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void resolveLeavingPlayers() {

        synchronized(server.getLeavingClients()) {
            for(ServerPlayerGateway gateway : server.getLeavingClients()) {
                Iterator<ExpiPlayer> it = players.listIterator();
                while(it.hasNext()) {
                    ExpiPlayer p = it.next();
                    if(gateway == p.getGateway()) {
                        it.remove();
                        for(ExpiPlayer pp : players) {
                            pp.getGateway().getManager().putEntityDespawnPacket(p);
                        }
                        p.destroySafe();
                        System.out.println("Player "+p.getName()+" left the server.");
                        server.getEntitiesID().remove(p.getID());
                        break;
                    }
                }
            }
            server.getLeavingClients().clear();
        }
    }

    private void resolveJoiningPlayers() {

        List<ExpiPlayer> joiningPlayers = new ArrayList<>();

        synchronized(server.getJoiningClients()) {
            // read init data from client
            for(ServerPlayerGateway gateway : server.getJoiningClients()) {
                PacketInputStream in = gateway.getIn();
                int packetID = in.getInt();
                String name = in.getString();
                ExpiPlayer ep = new ExpiPlayer(GameServer.get().getWorld().getSaveLocationForSpawn(), gateway, name);
                joiningPlayers.add(ep);
                server.getWorldLoaders().add(new WorldLoader(ep));
            }
            server.getJoiningClients().clear();
        }

        for(ExpiPlayer newPlayer : joiningPlayers) {

            // create list of entities (for new players)
            List<ExpiEntity> alreadyExistingEntities = new ArrayList<>();
            for(ExpiEntity e : GameServer.get().getEntities()) {
                if(e == newPlayer) continue;
                alreadyExistingEntities.add(e);
            }

            // initial packet for new player
            newPlayer.getGateway().getManager().putInitDataPacket(server.getWorld().getTerrain(), newPlayer, alreadyExistingEntities);
            newPlayer.getGateway().getOut().swap();

            synchronized(newPlayer.getGateway().getJoinLock()) {
                newPlayer.getGateway().getJoinLock().notify();
            }

            // notify all players about new players
            for(ExpiPlayer p : players) {
                if(p == newPlayer) continue;
                p.getGateway().getManager().putEntitySpawnPacket(newPlayer);
            }
        }
        joiningPlayers.clear();
    }

    private void resolvePlayersActions() {

        for(ExpiPlayer p : players) {

            PacketInputStream in = p.getGateway().getIn();

            //System.out.println("Server avail packets: "+in.getAvailablePackets());

            for(int i = 0; i < in.getAvailablePackets(); i++) {

                int packetID = in.getInt();
                //System.out.println("S: PID: " + packetID);

                switch(packetID) {

                    case 14: //TSPacket
                        p.updateThumbSticks(in);
                        break;

                    case 16: //InteractPacket
                        float x = in.getFloat();
                        float y = in.getFloat();
                        InteractType type = InteractType.getType(in.getInt());
                        server.getWorld().getInteractHandler().onInteract(p, x, y, type);
                        break;

                    case 25: {//InvItemMoveReqPacket
                        boolean main = in.getBoolean();
                        IntVector2 pos1 = in.getIntVector();
                        boolean main2 = in.getBoolean();
                        IntVector2 pos2 = in.getIntVector();
                        p.getInv().onMoveReq(p, pos1, pos2);
                        break;
                    }
                    case 26: {//InvItemMakeReqPacket
                        ItemRecipe recipe = ItemRecipe.getRecipe(in.getInt());
                        p.wantsToMakeItem(recipe);
                        break;
                    }
                    case 29://InvInteractPacket
                        p.getInv().onInteract(InvInteractType.getType(in.getInt()));
                }
            }
        }
    }

    public Object getTickLock() {
        return tickLock;
    }

    @Override
    public void end() {
        super.end();
        GameServer.get().getServerGateway().end();
    }
}
