package com.astetyne.main.net.server;

import com.astetyne.main.net.TerminableLooper;
import com.astetyne.main.net.client.actions.*;
import com.astetyne.main.net.netobjects.*;
import com.astetyne.main.net.server.actions.*;
import com.astetyne.main.net.server.entities.ServerDroppedItem;
import com.astetyne.main.net.server.entities.ServerEntity;
import com.astetyne.main.net.server.entities.ServerPlayer;
import com.astetyne.main.utils.Constants;
import com.astetyne.main.world.TileType;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TickLooper extends TerminableLooper {

    private final Object tickLock;
    private final GameServer server;
    private final List<MessageAction> tickGeneratedActions;
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

                recalculateDroppedItems();

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

        // create list of entities (for new players)
        List<SEntity> alreadyExistingEntities = new ArrayList<>();
        for(ServerPlayer cp : players) {
            alreadyExistingEntities.add(new SPlayer(cp));
        }
        for(ServerDroppedItem item : server.getDroppedItems()) {
            alreadyExistingEntities.add(new SDroppedItem(item));
        }

        for(ServerPlayer newPlayer : joiningPlayers) {

            // initial packet for new player
            InitDataActionS ida = new InitDataActionS(newPlayer.getID(), new SVector(newPlayer.getLocation()), alreadyExistingEntities, Constants.CHUNKS_NUMBER);
            ChunkFeedActionS cfa = new ChunkFeedActionS(server.getServerWorld().getChunk(0));
            List<MessageAction> initActions = new ArrayList<>();
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
            for(MessageAction ca : player.getGateway().getClientActions()) {
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
                    tickGeneratedActions.add(new EntityMoveActionCS(player.getID(), pma.getNewLocation(), pma.getVelocity(), 0));
                }else if(ca instanceof TileBreakActionC) {
                    TileBreakActionC tba = (TileBreakActionC) ca;
                    STile tile = server.getServerWorld().getChunk(tba.getChunkID()).getTerrain()[tba.getY()][tba.getX()];
                    if(tile.getType() == TileType.AIR) continue;
                    tile.setType(TileType.AIR);
                    float off = (1 - Constants.D_I_SIZE)/2;
                    Vector2 loc = new Vector2(tba.getX()+off, tba.getY()+off);
                    ServerDroppedItem droppedItem = new ServerDroppedItem(loc, tba.getDropItem(), Constants.SERVER_DEFAULT_TPS);
                    server.getDroppedItems().add(droppedItem);
                    tickGeneratedActions.add(new TileBreakActionS(tba.getChunkID(), tba.getX(), tba.getY(), droppedItem.getID()));
                    server.getServerWorld().onTileBreak(tba);
                }else if(ca instanceof EntityMoveActionCS) {
                    EntityMoveActionCS ema = (EntityMoveActionCS) ca;
                    ServerDroppedItem item = (ServerDroppedItem) server.getEntitiesID().get(ema.getEntityID());
                    item.getLocation().set(ema.getNewLocation().toVector());
                    item.getVelocity().set(ema.getVelocity().toVector());
                    item.setAngle(ema.getAngle());
                    tickGeneratedActions.add(ema);
                }else if(ca instanceof TilePlaceActionCS) {
                    TilePlaceActionCS tpa = (TilePlaceActionCS) ca;
                    STile tile = server.getServerWorld().getChunk(tpa.getChunkID()).getTerrain()[tpa.getY()][tpa.getX()];
                    if(tile.getType() != TileType.AIR) continue;
                    server.getServerWorld().tryToPlaceTile(tpa);
                }
            }
        }
    }

    private void recalculateDroppedItems() {

        Iterator<ServerDroppedItem> it = server.getDroppedItems().iterator();
        outer:
        while(it.hasNext()) {
            ServerDroppedItem item = it.next();
            if(item.getCooldown() != 0) {
                item.reduceCooldown();
                continue;
            }
            Vector2 center = item.getCenter();
            for(ServerPlayer p : players) {
                Vector2 dif = p.getCenter().sub(center);
                if(dif.len() < Constants.D_I_PICK_DIST) {
                    it.remove();
                    p.getGateway().addServerAction(new ItemPickupAction(item.getType()));
                    tickGeneratedActions.add(new ItemDespawnAction(item.getID(), item.getType()));
                    continue outer;
                }
            }
            int remainingTicks = item.getTicksToDespawn();
            if(remainingTicks == 0) {
                tickGeneratedActions.add(new ItemDespawnAction(item.getID(), item.getType()));
                it.remove();
                continue;
            }
            item.setTicksToDespawn(remainingTicks-1);
        }

        if(server.getPlayers().size() != 0) {
            server.getPlayers().get(0).getGateway().addServerAction(new PositionsRequestAction());
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

    public List<MessageAction> getTickActions() {
        return tickGeneratedActions;
    }
}
