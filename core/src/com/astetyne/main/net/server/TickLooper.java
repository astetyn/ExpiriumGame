package com.astetyne.main.net.server;

import com.astetyne.main.Constants;
import com.astetyne.main.net.TerminableLooper;
import com.astetyne.main.net.client.actions.*;
import com.astetyne.main.net.netobjects.SPlayer;
import com.astetyne.main.net.netobjects.STileData;
import com.astetyne.main.net.netobjects.SVector;
import com.astetyne.main.net.netobjects.SWorldChunk;
import com.astetyne.main.net.server.actions.*;
import com.astetyne.main.world.TileType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TickLooper extends TerminableLooper {

    private final Object tickLock;
    private final GameServer server;
    private final List<ServerAction> tickGeneratedActions;
    private final List<ServerPlayer> players;

    public TickLooper() {
        tickLock = new Object();
        server = GameServer.getServer();
        tickGeneratedActions = new ArrayList<>();
        players = server.getPlayers();
    }

    @Override
    public void run() {

        try {

            while(isRunning()) {

                resolveLeavingPlayers();

                resolveJoiningPlayers();

                resolvePlayersActions();

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
                Iterator<ServerPlayer> it = players.listIterator();
                while(it.hasNext()) {
                    ServerPlayer p = it.next();
                    if(gateway == p.getGateway()) {
                        it.remove();
                        PlayerLeaveActionS pla = new PlayerLeaveActionS(p.getID());
                        tickGeneratedActions.add(pla);
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

        List<ServerPlayer> joiningPlayers = new ArrayList<>();

        synchronized(server.getJoiningClients()) {
            // read init data from client
            for(ServerPlayerGateway gateway : server.getJoiningClients()) {
                JoinRequestActionC jra = (JoinRequestActionC) gateway.getClientActions().get(0);
                joiningPlayers.add(new ServerPlayer(GameServer.getServer().getServerWorld().getSaveLocation(), gateway, jra.getName()));
            }
            server.getJoiningClients().clear();
        }

        players.addAll(joiningPlayers);

        // create list of connected players (for new players)
        List<SPlayer> alreadyConnectedPlayers = new ArrayList<>();
        for(ServerPlayer cp : players) {
            alreadyConnectedPlayers.add(new SPlayer(cp.getID(), new SVector(cp.getLocation())));
        }

        for(ServerPlayer newPlayer : joiningPlayers) {

            // initial packet for new player
            InitDataActionS ida = new InitDataActionS(newPlayer.getID(), new SVector(newPlayer.getLocation()), alreadyConnectedPlayers, Constants.CHUNKS_NUMBER);
            ChunkFeedActionS cfa = new ChunkFeedActionS(server.getServerWorld().getChunk(0));
            List<ServerAction> initActions = new ArrayList<>();
            initActions.add(ida);
            initActions.add(cfa);
            newPlayer.getGateway().addServerActions(initActions);

            synchronized(newPlayer.getGateway().getJoinLock()) {
                newPlayer.getGateway().getJoinLock().notify();
            }

            // notify all players about new players
            PlayerJoinActionS psa = new PlayerJoinActionS(newPlayer);
            for(ServerPlayer cp : players) {
                if(cp == newPlayer) {
                    continue;
                }
                cp.getGateway().addServerAction(psa);
            }
        }
        joiningPlayers.clear();
    }

    private void resolvePlayersActions() {

        for(ServerPlayer player : server.getPlayers()) {
            for(ClientAction ca : player.getGateway().getClientActions()) {
                if(ca instanceof ChunkRequestActionC) {
                    ChunkRequestActionC cra = (ChunkRequestActionC) ca;
                    SWorldChunk chunk = server.getServerWorld().getChunk(cra.getChunkId());
                    ChunkFeedActionS cfa = new ChunkFeedActionS(chunk);
                    player.getGateway().addServerAction(cfa);
                }else if(ca instanceof PlayerMoveActionC) {
                    PlayerMoveActionC pma = (PlayerMoveActionC) ca;
                    ServerEntity e = server.getEntitiesID().get(player.getID());
                    e.getLocation().x = pma.getNewLocation().getX();
                    e.getLocation().y = pma.getNewLocation().getY();
                    tickGeneratedActions.add(new EntityMoveActionS(player.getID(), pma.getNewLocation(), pma.getVelocity()));
                }else if(ca instanceof TileBreakActionC) {
                    TileBreakActionC tba = (TileBreakActionC) ca;
                    STileData tile = server.getServerWorld().getChunk(tba.getChunkID()).getTerrain()[tba.getY()][tba.getX()];
                    if(tile.getType() == TileType.AIR) continue;
                    tile.setType(TileType.AIR);
                    tickGeneratedActions.add(new TileBreakActionS(tba.getChunkID(), tba.getX(), tba.getY()));
                }
            }
        }
    }

    private void sendGeneratedActions() {
        for(ServerPlayer player : server.getPlayers()) {
            player.getGateway().addServerActions(tickGeneratedActions);
        }
        tickGeneratedActions.clear();
    }

    public Object getTickLock() {
        return tickLock;
    }

    @Override
    public void end() {
        super.end();
        GameServer.getServer().getServerGateway().end();
    }
}
