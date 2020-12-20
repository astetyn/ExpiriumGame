package com.astetyne.server.backend;

import com.astetyne.main.net.client.packets.JoinRequestPacket;
import com.astetyne.main.utils.Constants;
import com.astetyne.server.GameServer;
import com.astetyne.server.api.entities.ExpiPlayer;
import com.astetyne.server.backend.packables.PackableEntity;
import com.astetyne.server.backend.packets.ChunkFeedPacket;
import com.astetyne.server.backend.packets.InitDataPacket;

import java.nio.ByteBuffer;
import java.util.ArrayList;
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

                //resolveLeavingPlayers();

                resolveJoiningPlayers();

                //resolvePlayersActions();

                //recalculateDroppedItems();

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

    /*private void resolveLeavingPlayers() {

        synchronized(server.getLeavingClients()) {
            for(ServerPlayerGateway gateway : server.getLeavingClients()) {
                Iterator<ExpiPlayer> it = players.listIterator();
                while(it.hasNext()) {
                    ExpiPlayer p = it.next();
                    if(gateway == p.getGateway()) {
                        it.remove();
                        PlayerLeaveActionS pla = new PlayerLeaveActionS(p.getID());
                        tickSubPackets.add(pla);
                        System.out.println("Player "+p.getName()+" left the server.");
                        server.getEntitiesID().remove(p.getID());
                        break;
                    }
                }
            }
            server.getLeavingClients().clear();
        }
    }*/

    private void resolveJoiningPlayers() {

        List<ExpiPlayer> joiningPlayers = new ArrayList<>();

        synchronized(server.getJoiningClients()) {
            // read init data from client
            for(ServerPlayerGateway gateway : server.getJoiningClients()) {
                ByteBuffer bb = ByteBuffer.wrap(gateway.getClientIncomingPackets().get(0).bytes);
                int packetID = bb.getInt();
                JoinRequestPacket jra = new JoinRequestPacket(bb);
                joiningPlayers.add(new ExpiPlayer(GameServer.get().getWorld().getSaveLocation(), gateway, jra.getName()));
            }
            server.getJoiningClients().clear();
        }

        players.addAll(joiningPlayers);

        // create list of entities (for new players)
        List<PackableEntity> alreadyExistingEntities = new ArrayList<>();
        for(ExpiPlayer ep : players) {
            alreadyExistingEntities.add(new PackableEntity(ep));
        }
        //todo: aj pre dropped items

        for(ExpiPlayer newPlayer : joiningPlayers) {

            // initial packet for new player
            InitDataPacket ida = new InitDataPacket(Constants.CHUNKS_NUMBER, newPlayer.getID(), newPlayer.getLocation(), alreadyExistingEntities);
            ChunkFeedPacket cfa = new ChunkFeedPacket(server.getWorld().getChunk(0));
            List<Packable> initActions = new ArrayList<>();
            initActions.add(ida);
            initActions.add(cfa);
            newPlayer.getGateway().addSubPackets(initActions);

            synchronized(newPlayer.getGateway().getJoinLock()) {
                newPlayer.getGateway().getJoinLock().notify();
            }

            // notify all players about new players
            /*PlayerJoinActionS psa = new PlayerJoinActionS(newPlayer);
            for(ExpiPlayer cp : players) {
                if(cp == newPlayer) {
                    continue;
                }
                cp.getGateway().addSubPacket(psa);
            }*/
        }
        joiningPlayers.clear();
    }

    /*private void resolvePlayersActions() {

        for(ExpiPlayer player : server.getPlayers()) {
            for(IncomingPacket ca : player.getGateway().getClientIncomingPackets()) {
                if(ca instanceof PlayerMoveActionC) {
                    PlayerMoveActionC pma = (PlayerMoveActionC) ca;
                    ExpiEntity e = server.getEntitiesID().get(player.getID());
                    e.getLocation().x = pma.getNewLocation().getX();
                    e.getLocation().y = pma.getNewLocation().getY();
                    tickGeneratedActions.add(new EntityMoveActionCS(player.getID(), pma.getNewLocation(), pma.getVelocity(), 0));
                }else if(ca instanceof TileBreakActionC) {
                    TileBreakActionC tba = (TileBreakActionC) ca;
                    ExpiTile tile = server.getWorld().getChunk(tba.getChunkID()).getTerrain()[tba.getY()][tba.getX()];
                    if(tile.getType() == TileType.AIR) continue;
                    tile.setType(TileType.AIR);
                    float off = (1 - Constants.D_I_SIZE)/2;
                    Vector2 loc = new Vector2(tba.getX()+off, tba.getY()+off);
                    ExpiDroppedItem droppedItem = new ExpiDroppedItem(loc, tba.getDropItem(), Constants.SERVER_DEFAULT_TPS);
                    server.getDroppedItems().add(droppedItem);
                    tickGeneratedActions.add(new TileBreakActionS(tba.getChunkID(), tba.getX(), tba.getY(), droppedItem.getID()));
                    server.getWorld().onTileBreak(tba);
                }else if(ca instanceof EntityMoveActionCS) {
                    EntityMoveActionCS ema = (EntityMoveActionCS) ca;
                    ExpiDroppedItem item = (ExpiDroppedItem) server.getEntitiesID().get(ema.getEntityID());
                    item.getLocation().set(ema.getNewLocation().toVector());
                    item.getVelocity().set(ema.getVelocity().toVector());
                    item.setAngle(ema.getAngle());
                    tickGeneratedActions.add(ema);
                }else if(ca instanceof TilePlaceActionCS) {
                    TilePlaceActionCS tpa = (TilePlaceActionCS) ca;
                    ExpiTile tile = server.getWorld().getChunk(tpa.getChunkID()).getTerrain()[tpa.getY()][tpa.getX()];
                    if(tile.getType() != TileType.AIR) continue;
                    server.getWorld().tryToPlaceTile(tpa);
                }
            }
        }
    }

    private void recalculateDroppedItems() {

        Iterator<ExpiDroppedItem> it = server.getDroppedItems().iterator();
        outer:
        while(it.hasNext()) {
            ExpiDroppedItem item = it.next();
            if(item.getCooldown() != 0) {
                item.reduceCooldown();
                continue;
            }
            Vector2 center = item.getCenter();
            for(ExpiPlayer p : players) {
                Vector2 dif = p.getCenter().sub(center);
                if(dif.len() < Constants.D_I_PICK_DIST) {
                    it.remove();
                    p.getGateway().addSubPacket(new ItemPickupAction(item.getType()));
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
            server.getPlayers().get(0).getGateway().addSubPacket(new PositionsRequestAction());
        }

    }*/

    private void sendGeneratedActions() {
        for(ExpiPlayer player : server.getPlayers()) {
            player.getGateway().addSubPackets(tickSubPackets);
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
