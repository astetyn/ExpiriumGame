package com.astetyne.main.stages;

import com.astetyne.main.gui.GameGUILayout;
import com.astetyne.main.items.inventory.Inventory;
import com.astetyne.main.world.GameWorld;
import com.astetyne.server.backend.IncomingPacket;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

import java.nio.ByteBuffer;
import java.util.List;

public class GameStage extends ExpiStage {

    private static GameStage game;

    private GameWorld gameWorld;

    private final Inventory inventory;

    private final Box2DDebugRenderer b2dr;

    private final GameGUILayout gameGUI;

    public GameStage() {

        game = this;

        b2dr = new Box2DDebugRenderer();

        System.out.println("DENSITY: "+Gdx.graphics.getDensity());

        inventory = new Inventory();

        gameGUI = new GameGUILayout();

        stage.addActor(gameGUI.getTable());

        resize();

    }

    @Override
    public void update() {

        stage.act();
        gameWorld.update();
        gameGUI.update();

    }

    @Override
    public void render() {

        Gdx.gl.glClearColor(0.6f, 0.8f, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        gameWorld.render();
        batch.end();

        stage.draw();

        b2dr.render(gameWorld.getB2dWorld(), gameWorld.getCamera().combined);

    }

    @Override
    public void resize() {

        if(gameWorld != null) gameWorld.resize();
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

    }

    @Override
    public void dispose() {
        super.dispose();
        gameWorld.dispose();
    }

    @Override
    public void onServerUpdate(List<IncomingPacket> packets) {

        for(IncomingPacket packet : packets) {

            ByteBuffer bb = ByteBuffer.wrap(packet.bytes);
            int subPackets = bb.getInt();
            System.out.println("C: subapackets: "+subPackets);

            for(int i = 0; i < subPackets; i++) {

                int packetID = bb.getInt();
                System.out.println("C: PID: "+packetID);

                switch(packetID) {

                    case 11: //InitDataPacket
                        int numberOfChunks = bb.getInt();
                        gameWorld = new GameWorld(numberOfChunks);
                        gameWorld.postSetup(bb);
                        break;
                    case 13: //ChunkFeedPacket
                        gameWorld.feedChunk(bb);
                        break;

                }

            }

            /*if(packet instanceof PlayerJoinActionS) {
                PlayerJoinActionS psa = (PlayerJoinActionS) packet;
                PlayerEntity pe = new PlayerEntity(psa.getPlayerID(), psa.getLocation().toVector());

            }else if(packet instanceof EntityMoveActionCS) {
                EntityMoveActionCS ema = (EntityMoveActionCS) packet;
                int id = ema.getEntityID();
                if(id == gameWorld.getPlayer().getID() || !gameWorld.getEntitiesID().containsKey(id)) continue;
                gameWorld.getEntitiesID().get(ema.getEntityID()).onMoveAction(ema);

            }else if(packet instanceof PlayerLeaveActionS) {
                PlayerLeaveActionS pla = (PlayerLeaveActionS) packet;
                PlayerEntity p = (PlayerEntity) gameWorld.getEntitiesID().get(pla.getPlayerID());
                gameWorld.destroyEntity(p);

            }else if(packet instanceof TileBreakActionS) {
                TileBreakActionS tba = (TileBreakActionS) packet;
                WorldChunk chunk = gameWorld.getChunks()[tba.getChunkID()];
                if(chunk == null) continue;
                Tile t = chunk.getTerrain()[tba.getY()][tba.getX()];
                ItemType drop = t.getTileExtraData().getItemOnDrop();
                t.destroy();
                int id = tba.getItemDropID();
                DroppedItemEntity dip = new DroppedItemEntity(id, drop.initItem(), tba.getItemAngleVel(), t.getCenterLoc());

            }else if(packet instanceof ItemPickupAction) {
                ItemPickupAction ipa = (ItemPickupAction) packet;
                inventory.onItemPick(ipa.getItem().initItem());

            }else if(packet instanceof ItemDespawnAction) {
                ItemDespawnAction ida = (ItemDespawnAction) packet;
                DroppedItemEntity dip = (DroppedItemEntity) gameWorld.getEntitiesID().get(ida.getID());
                gameWorld.destroyEntity(dip);

            }else if(packet instanceof PositionsRequestAction) {
                for(Entity e : gameWorld.getEntities()) {
                    if(e instanceof DroppedItemEntity) {
                        ExpiriumGame.get().getClientGateway().addSubPacket(new EntityMoveActionCS(e));
                    }
                }

            }else if(packet instanceof TilePlaceActionCS) {
                TilePlaceActionCS tpa = (TilePlaceActionCS) packet;
                if(gameWorld.getPlayer().getID() == tpa.getPlayerID()) {
                    inventory.removeItem(tpa.getPlacedItem());
                }
                WorldChunk wch = gameWorld.getChunks()[tpa.getChunkID()];
                if(wch == null) continue;
                Tile t = wch.getTerrain()[tpa.getY()][tpa.getX()];
                TileExtraData data = tpa.getPlacedItem().initDefaultData();
                wch.changeTile(t.getX(), t.getY(), data);
            }*/
        }
        //ExpiriumGame.get().getClientGateway().addSubPacket(gameWorld.getPlayer().generateMoveAction());
    }

    @Override
    public void onServerFail() {

    }

    public GameGUILayout getGameGUI() {
        return gameGUI;
    }

    public Inventory getInv() {
        return inventory;
    }

    public GameWorld getWorld() {
        return gameWorld;
    }

    public static int toPixels(int realLen) {
        return (int) (realLen * Gdx.graphics.getDensity());
    }

    public static GameStage get() {
        return game;
    }
}
