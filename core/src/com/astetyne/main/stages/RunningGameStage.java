package com.astetyne.main.stages;

import com.astetyne.main.ExpiriumGame;
import com.astetyne.main.entity.PlayerEntity;
import com.astetyne.main.gui.GameGUILayout;
import com.astetyne.main.items.inventory.Inventory;
import com.astetyne.main.net.server.actions.*;
import com.astetyne.main.world.GameWorld;
import com.astetyne.main.world.WorldChunk;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

import java.util.List;

public class RunningGameStage extends ExpiStage {

    private GameWorld gameWorld;

    private final Inventory inventory;

    private final Box2DDebugRenderer b2dr;

    private final GameGUILayout gameGUI;

    public RunningGameStage() {

        b2dr = new Box2DDebugRenderer();

        System.out.println("DENSITY: "+Gdx.graphics.getDensity());

        inventory = new Inventory(this);

        gameGUI = new GameGUILayout(this);

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

        batch.enableBlending();

        batch.begin();
        gameWorld.render();
        batch.end();

        stage.draw();

        b2dr.render(gameWorld.getB2dWorld(), gameWorld.getCamera().combined.cpy().scl(GameWorld.PPM));

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
    public void onServerUpdate(List<ServerAction> actions) {

        for(ServerAction serverAction : actions) {

            if(serverAction instanceof InitDataActionS) {
                InitDataActionS initData = (InitDataActionS) serverAction;
                gameWorld = new GameWorld(batch, this, initData);

            }else if(serverAction instanceof ChunkFeedActionS) {
                ChunkFeedActionS action = (ChunkFeedActionS) serverAction;
                gameWorld.feedChunk(action.getChunk());

            }else if(serverAction instanceof PlayerJoinActionS) {
                PlayerJoinActionS psa = (PlayerJoinActionS) serverAction;
                gameWorld.createPlayerEntity(psa.getPlayerID(), psa.getLocation().toVector());

            }else if(serverAction instanceof EntityMoveActionS) {
                EntityMoveActionS ema = (EntityMoveActionS) serverAction;
                gameWorld.onEntityMove(ema);

            }else if(serverAction instanceof PlayerLeaveActionS) {
                PlayerLeaveActionS pla = (PlayerLeaveActionS) serverAction;
                PlayerEntity p = (PlayerEntity) gameWorld.getEntitiesID().get(pla.getPlayerID());
                gameWorld.getOtherPlayers().remove(p);
                gameWorld.destroyEntity(p);

            }else if(serverAction instanceof TileBreakActionS) {
                TileBreakActionS tba = (TileBreakActionS) serverAction;
                WorldChunk chunk = gameWorld.getChunks()[tba.getChunkID()];
                if(chunk == null) return;
                chunk.getTerrain()[tba.getY()][tba.getX()].destroy();
            }
        }

        gameWorld.checkChunks();

        ExpiriumGame.getGame().getClientGateway().addAction(gameWorld.getPlayer().generateMoveAction());

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

    public GameWorld getGameWorld() {
        return gameWorld;
    }

    public static int toPixels(int realLen) {
        return (int) (realLen * Gdx.graphics.getDensity());
    }
}
