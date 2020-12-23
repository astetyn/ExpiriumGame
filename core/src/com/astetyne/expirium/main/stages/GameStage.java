package com.astetyne.expirium.main.stages;

import com.astetyne.expirium.main.ExpiriumGame;
import com.astetyne.expirium.main.entity.Entity;
import com.astetyne.expirium.main.entity.EntityType;
import com.astetyne.expirium.main.gui.GameGUILayout;
import com.astetyne.expirium.main.items.ItemType;
import com.astetyne.expirium.main.items.inventory.Inventory;
import com.astetyne.expirium.main.world.GameWorld;
import com.astetyne.expirium.server.backend.IncomingPacket;
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

    int traffic = 0;
    long time  = System.currentTimeMillis();

    @Override
    public void onServerUpdate(List<IncomingPacket> packets) {

        for(IncomingPacket packet : packets) {

            traffic += packet.bytes.length;
            if(time + 2000 < System.currentTimeMillis()) {
                time = System.currentTimeMillis();
                System.out.println("Client traffic: "+traffic);
                traffic = 0;
            }

            ByteBuffer bb = ByteBuffer.wrap(packet.bytes);
            int subPackets = bb.getInt(); //System.out.println("C: incoming subpackets: "+subPackets);
            for(int i = 0; i < subPackets; i++) {

                int packetID = bb.getInt(); //System.out.println("PID: "+packetID);

                switch(packetID) {

                    case 11: //InitDataPacket
                        int numberOfChunks = bb.getInt();
                        gameWorld = new GameWorld(numberOfChunks);
                        gameWorld.postSetup(bb);
                        break;

                    case 12: //ChunkDestroyPacket
                        gameWorld.onDestroyChunkEvent(bb);
                        break;

                    case 13: //ChunkFeedPacket
                        gameWorld.onFeedChunkEvent(bb);
                        break;

                    case 17: //BreakTileAckPacket
                        gameWorld.onBreakTileEvent(bb);
                        break;

                    case 18: //BreakPlaceAckPacket
                        gameWorld.onPlaceTileEvent(bb);
                        break;

                    case 19: //EntityMovePacket
                        int eID = bb.getInt();
                        Entity e = gameWorld.getEntitiesID().get(eID);
                        if(e != null) {
                            e.onMove(bb);
                        }else {
                            bb.position(bb.position()+6*4);
                        }
                        break;

                    case 20: //EntitySpawnPacket
                        EntityType.getType(bb.getInt()).initEntity(bb);
                        break;

                    case 21: //EntityDespawnPacket
                        int id = bb.getInt();
                        gameWorld.getEntitiesID().get(id).destroy();
                        break;

                    case 22: //ItemPickupPacket
                        inventory.onItemPick(ItemType.getType(bb.getInt()).initItem());
                        break;

                }
            }
        }
        ExpiriumGame.get().getClientGateway().addSubPacket(gameWorld.getPlayer().generateMoveAction());
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
