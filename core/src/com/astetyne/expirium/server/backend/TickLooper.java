package com.astetyne.expirium.server.backend;

import com.astetyne.expirium.main.items.ItemType;
import com.astetyne.expirium.main.utils.Constants;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.entities.ExpiEntity;
import com.astetyne.expirium.server.api.entities.ExpiPlayer;
import com.astetyne.expirium.server.backend.packables.PackableEntity;
import com.astetyne.expirium.server.backend.packets.EntityDespawnPacket;
import com.astetyne.expirium.server.backend.packets.EntitySpawnPacket;
import com.astetyne.expirium.server.backend.packets.InitDataPacket;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TickLooper extends TerminableLooper {

    private final Object tickLock;
    private final GameServer server;
    private final List<Packable> tickSubPackets;
    private final List<ExpiPlayer> players;

    public TickLooper() {
        tickLock = new Object();
        server = GameServer.get();
        tickSubPackets = new ArrayList<>();
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

                sendGeneratedActions();

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
                        EntityDespawnPacket edp = new EntityDespawnPacket(p);
                        p.destroySafe();
                        tickSubPackets.add(edp);
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
                ByteBuffer bb = ByteBuffer.wrap(gateway.getClientIncomingPackets().get(0).bytes);
                int packetID = bb.getInt();
                int nameLen = bb.getInt();
                StringBuilder sb = new StringBuilder();
                for(int i = 0; i < nameLen; i++) {
                    sb.append(bb.getChar());
                }
                joiningPlayers.add(new ExpiPlayer(GameServer.get().getWorld().getSaveLocationForSpawn(), gateway, sb.toString()));
            }
            server.getJoiningClients().clear();
        }

        for(ExpiPlayer newPlayer : joiningPlayers) {

            // create list of entities (for new players)
            List<PackableEntity> alreadyExistingEntities = new ArrayList<>();
            for(ExpiEntity e : GameServer.get().getEntities()) {
                if(e == newPlayer) continue;
                alreadyExistingEntities.add(new PackableEntity(e));
            }

            // initial packet for new player
            InitDataPacket ida = new InitDataPacket(Constants.CHUNKS_NUMBER, newPlayer.getID(), newPlayer.getLocation(), alreadyExistingEntities);
            List<Packable> initActions = new ArrayList<>();
            initActions.add(ida);
            newPlayer.getGateway().addSubPackets(initActions);

            synchronized(newPlayer.getGateway().getJoinLock()) {
                newPlayer.getGateway().getJoinLock().notify();
            }

            // notify all players about new players
            EntitySpawnPacket esp = new EntitySpawnPacket(newPlayer);
            for(ExpiPlayer p : players) {
                if(p == newPlayer) continue;
                p.getGateway().addSubPacket(esp);
            }
        }
        joiningPlayers.clear();
    }

    private void resolvePlayersActions() {

        for(ExpiPlayer p : players) {
            for(IncomingPacket packet : p.getGateway().getClientIncomingPackets()) {

                ByteBuffer bb = ByteBuffer.wrap(packet.bytes);
                int subPackets = bb.getInt(); //System.out.println("S: incoming subpackets: "+subPackets);
                for(int i = 0; i < subPackets; i++) {

                    int packetID = bb.getInt();
                    //System.out.println("S: PID: " + packetID);

                    switch(packetID) {

                        case 14: //PlayerMovePacket
                            ExpiPlayer e = (ExpiPlayer) server.getEntitiesID().get(p.getID());
                            e.onMove(bb.getFloat(), bb.getFloat(), bb.getFloat(), bb.getFloat());
                            break;

                        case 15: //TileBreakReqPacket
                            server.getWorld().onTileBreakReq(bb.getInt(), bb.getInt(), bb.getInt(), p);
                            break;

                        case 16: //TilePlaceReqPacket
                            server.getWorld().onTilePlaceReq(bb.getInt(), bb.getInt(), bb.getInt(), ItemType.getType(bb.getInt()), p);
                            break;
                    }
                }
            }
        }
    }

    private void sendGeneratedActions() {
        for(ExpiPlayer p : server.getPlayers()) {
            p.getGateway().addSubPackets(tickSubPackets);
        }
        tickSubPackets.clear();
    }

    public Object getTickLock() {
        return tickLock;
    }

    @Override
    public void end() {
        super.end();
        GameServer.get().getServerGateway().end();
    }

    public List<Packable> getTickSubPackets() {
        return tickSubPackets;
    }
}
