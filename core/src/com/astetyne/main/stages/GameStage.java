package com.astetyne.main.stages;

import com.astetyne.main.ExpiriumGame;
import com.astetyne.main.entity.DroppedItemEntity;
import com.astetyne.main.entity.Entity;
import com.astetyne.main.entity.PlayerEntity;
import com.astetyne.main.gui.GameGUILayout;
import com.astetyne.main.items.ItemType;
import com.astetyne.main.items.inventory.Inventory;
import com.astetyne.main.net.netobjects.MessageAction;
import com.astetyne.main.net.server.actions.*;
import com.astetyne.main.world.GameWorld;
import com.astetyne.main.world.WorldChunk;
import com.astetyne.main.world.tiles.Tile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

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
    public void onServerUpdate(List<MessageAction> actions) {

        for(MessageAction serverAction : actions) {

            if(serverAction instanceof InitDataActionS) {
                InitDataActionS initData = (InitDataActionS) serverAction;
                gameWorld = new GameWorld(initData);
                gameWorld.postSetup(initData);

            }else if(serverAction instanceof ChunkFeedActionS) {
                ChunkFeedActionS action = (ChunkFeedActionS) serverAction;
                gameWorld.feedChunk(action.getChunk());

            }else if(serverAction instanceof PlayerJoinActionS) {
                PlayerJoinActionS psa = (PlayerJoinActionS) serverAction;
                PlayerEntity pe = new PlayerEntity(psa.getPlayerID(), psa.getLocation().toVector());

            }else if(serverAction instanceof EntityMoveActionCS) {
                EntityMoveActionCS ema = (EntityMoveActionCS) serverAction;
                int id = ema.getEntityID();
                if(id == gameWorld.getPlayer().getID() || !gameWorld.getEntitiesID().containsKey(id)) continue;
                gameWorld.getEntitiesID().get(ema.getEntityID()).onMoveAction(ema);

            }else if(serverAction instanceof PlayerLeaveActionS) {
                PlayerLeaveActionS pla = (PlayerLeaveActionS) serverAction;
                PlayerEntity p = (PlayerEntity) gameWorld.getEntitiesID().get(pla.getPlayerID());
                gameWorld.destroyEntity(p);

            }else if(serverAction instanceof TileBreakActionS) {
                TileBreakActionS tba = (TileBreakActionS) serverAction;
                WorldChunk chunk = gameWorld.getChunks()[tba.getChunkID()];
                if(chunk == null) continue;
                Tile t = chunk.getTerrain()[tba.getY()][tba.getX()];
                t.destroy();
                ItemType drop = t.getTileExtraData().getItemOnDrop();
                int id = tba.getItemDropID();
                DroppedItemEntity dip = new DroppedItemEntity(id, drop, tba.getItemAngleVel(), t.getCenterLoc());

            }else if(serverAction instanceof ItemPickupAction) {
                ItemPickupAction ipa = (ItemPickupAction) serverAction;
                inventory.onItemPick(ipa.getItem());

            }else if(serverAction instanceof ItemDespawnAction) {
                ItemDespawnAction ida = (ItemDespawnAction) serverAction;
                DroppedItemEntity dip = (DroppedItemEntity) gameWorld.getEntitiesID().get(ida.getID());
                gameWorld.destroyEntity(dip);

            }else if(serverAction instanceof PositionsRequestAction) {

                for(Entity e : gameWorld.getEntities()) {
                    if(e instanceof DroppedItemEntity) {
                        ExpiriumGame.get().getClientGateway().addAction(new EntityMoveActionCS(e));
                    }
                }
            }
        }
        gameWorld.checkChunks();
        ExpiriumGame.get().getClientGateway().addAction(gameWorld.getPlayer().generateMoveAction());
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
