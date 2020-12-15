package com.astetyne.main.stages;

import com.astetyne.main.ExpiriumGame;
import com.astetyne.main.ResourceManager;
import com.astetyne.main.entity.PlayerEntity;
import com.astetyne.main.gui.ThumbStick;
import com.astetyne.main.net.server.actions.*;
import com.astetyne.main.world.GameWorld;
import com.astetyne.main.world.WorldChunk;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import java.util.List;

public class RunningGameStage extends ExpiStage {

    private GameWorld gameWorld;
    private final ThumbStick movementTS;

    Label fpsLabel;

    private final Box2DDebugRenderer b2dr;

    public RunningGameStage() {

        b2dr = new Box2DDebugRenderer();

        System.out.println("DENSITY: "+Gdx.graphics.getDensity());

        Table table = new Table();

        movementTS = new ThumbStick(ResourceManager.THUMB_STICK_STYLE);
        fpsLabel = new Label("", ResourceManager.LABEL_STYLE);

        fpsLabel.setFontScale(0.5f);

        table.row();
        table.add(fpsLabel).width(200);
        table.row().expand();

        table.add(movementTS).padBottom(30 * Gdx.graphics.getDensity()).padLeft(30 * Gdx.graphics.getDensity()).align(Align.bottomLeft);

        table.setDebug(true);
        table.setFillParent(true);

        stage.addActor(table);

        resize();

    }

    @Override
    public void update() {

        stage.act();

        gameWorld.update();
        fpsLabel.setText("fps: "+Gdx.graphics.getFramesPerSecond());

        //System.out.println(player.getLocation());

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

        //sightTS.resize((int) (Gdx.graphics.getWidth() - 200 * Gdx.graphics.getDensity()),(int) ( 100 * Gdx.graphics.getDensity()));

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

    public ThumbStick getMovementTS() {
        return movementTS;
    }
}
