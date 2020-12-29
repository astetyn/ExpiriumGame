package com.astetyne.expirium.server.backend;

import com.astetyne.expirium.main.items.ItemType;
import com.astetyne.expirium.main.utils.Constants;
import com.astetyne.expirium.main.utils.IntVector2;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.entities.ExpiEntity;
import com.astetyne.expirium.server.api.entities.ExpiPlayer;

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

                server.getWorld().onTick();

                //todo: ai? time? weather?

                // wakes up all clients threads and send new actions
                synchronized(tickLock) {
                    tickLock.notifyAll();
                }
                //noinspection BusyWait
                Thread.sleep(1000/Constants.SERVER_DEFAULT_TPS);
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
                joiningPlayers.add(new ExpiPlayer(GameServer.get().getWorld().getSaveLocationForSpawn(), gateway, name));
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
            newPlayer.getGateway().getManager().putInitDataPacket(Constants.CHUNKS_NUMBER, newPlayer, alreadyExistingEntities);

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

                    case 14: //PlayerMovePacket
                        ExpiPlayer e = (ExpiPlayer) server.getEntitiesID().get(p.getID());
                        e.onMove(in.getFloat(), in.getFloat(), in.getFloat(), in.getFloat());
                        break;

                    case 15: //TileBreakReqPacket
                        server.getWorld().onTileBreakReq(in.getInt(), in.getInt(), in.getInt(), p);
                        break;

                    case 16: //TilePlaceReqPacket
                        server.getWorld().onTilePlaceReq(in.getInt(), in.getInt(), in.getInt(), ItemType.getType(in.getInt()), p);
                        break;

                    case 23: {//InvOpenReqPacket
                        int invID = in.getInt();
                        if(server.getInventoriesID().containsKey(invID)) {
                            p.getGateway().getManager().putInvFeedPacket(server.getInventoriesID().get(invID));
                        }
                        break;
                    }
                    case 25: {//InvItemMoveReqPacket
                        int invID = in.getInt();
                        IntVector2 pos1 = in.getIntVector();
                        IntVector2 pos2 = in.getIntVector();
                        if(server.getInventoriesID().containsKey(invID)) {
                            server.getInventoriesID().get(invID).onMoveReq(p, pos1, pos2);
                        }
                        break;
                    }
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
