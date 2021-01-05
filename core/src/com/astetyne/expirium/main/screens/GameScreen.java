package com.astetyne.expirium.main.screens;

import com.astetyne.expirium.main.ExpiGame;
import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.gui.stage.DoubleInventoryStage;
import com.astetyne.expirium.main.gui.stage.GameStage;
import com.astetyne.expirium.main.gui.stage.InventoryStage;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.main.world.GameWorld;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

public class GameScreen implements Screen, Gatewayable {

    private static GameScreen gameScreen;

    private final SpriteBatch batch;
    private final InputMultiplexer multiplexer;
    private final GameWorld gameWorld;
    private final Box2DDebugRenderer b2dr;
    private int serverTime;

    private final GameStage gameStage;
    private final InventoryStage invStage;
    private final DoubleInventoryStage doubleInvStage;

    public GameScreen(PacketInputStream in) {

        gameScreen = this;

        batch = ExpiGame.get().getBatch();
        multiplexer = new InputMultiplexer();
        gameWorld = new GameWorld();
        b2dr = new Box2DDebugRenderer();

        gameStage = new GameStage();
        invStage = new InventoryStage();
        doubleInvStage = new DoubleInventoryStage();
        gameStage.setVisible(true);

        Res.MAIN_FONT.getData().setScale((float)Gdx.graphics.getHeight() / Gdx.graphics.getWidth(), 1);

        multiplexer.addProcessor(gameStage);
        multiplexer.addProcessor(invStage);
        Gdx.input.setInputProcessor(multiplexer);

        // load init data from server
        gameWorld.loadData(in);

    }

    public void update() {

        gameStage.act();
        invStage.act();
        doubleInvStage.act();
        gameWorld.update();

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        update();

        Gdx.gl.glClearColor(0.6f, 0.8f, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float xShift1 = (gameWorld.getPlayer().getLocation().x*30) % 1000;
        float yShift1 = gameWorld.getPlayer().getLocation().y*30; // texture will end in y=100
        float xShift2 = (gameWorld.getPlayer().getLocation().x*50) % 1000;
        float yShift2 = gameWorld.getPlayer().getLocation().y*50;
        float xShift3 = (gameWorld.getPlayer().getLocation().x*80) % 1000;
        float yShift3 = gameWorld.getPlayer().getLocation().y*100;

        batch.begin();

        // parallax effect - needs projection matrix from gui (1000*1000)
        batch.draw(Res.BG_1, -xShift1, -yShift1, 1000, 3000);
        batch.draw(Res.BG_1, 1000-xShift1, -yShift1, 1000, 3000);
        batch.draw(Res.BG_2, -xShift2, -yShift2, 1000, 3000);
        batch.draw(Res.BG_2, 1000-xShift2, -yShift2, 1000, 3000);
        batch.draw(Res.BG_3, -xShift3, -yShift3, 1000, 3000);
        batch.draw(Res.BG_3, 1000-xShift3, -yShift3, 1000, 3000);

        gameWorld.render();

        // render black screen if stage is dimmed

        batch.end();

        gameStage.draw();
        invStage.draw();
        doubleInvStage.draw();

        if(Consts.DEBUG) b2dr.render(gameWorld.getB2dWorld(), gameWorld.getCamera().combined);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        gameWorld.dispose();
    }

    @Override
    public void onServerUpdate() {
        gameWorld.getPlayer().generateMovePacket();
    }

    @Override
    public void onServerFail() {

    }

    public void showGameStage() {
        gameStage.setVisible(true);
        invStage.setVisible(false);
        doubleInvStage.setVisible(false);
    }

    public void showInvStage() {
        gameStage.setVisible(false);
        invStage.setVisible(true);
        doubleInvStage.setVisible(false);
    }

    public void showDoubleInvStage() {
        gameStage.setVisible(false);
        invStage.setVisible(false);
        doubleInvStage.setVisible(true);
    }

    public GameWorld getWorld() {
        return gameWorld;
    }

    public static GameScreen get() {
        return gameScreen;
    }

    public int getServerTime() {
        return serverTime;
    }

    public void setServerTime(int serverTime) {
        this.serverTime = serverTime;
    }

    public InputMultiplexer getMultiplexer() {
        return multiplexer;
    }

    public GameStage getGameStage() {
        return gameStage;
    }

    public InventoryStage getInvStage() {
        return invStage;
    }

    public DoubleInventoryStage getDoubleInvStage() {
        return doubleInvStage;
    }
}
