package com.astetyne.expirium.main.screens;

import com.astetyne.expirium.main.ExpiGame;
import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.gui.stage.DoubleInventoryStage;
import com.astetyne.expirium.main.gui.stage.ExpiStage;
import com.astetyne.expirium.main.gui.stage.GameStage;
import com.astetyne.expirium.main.gui.stage.InventoryStage;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.main.world.GameWorld;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameScreen implements Screen, Gatewayable {

    private static GameScreen gameScreen;

    private final SpriteBatch batch;
    private final InputMultiplexer multiplexer;
    private final GameWorld gameWorld;
    private int serverTime;
    private int serverTPS;

    private final GameStage gameStage;
    private final InventoryStage invStage;
    private final DoubleInventoryStage doubleInvStage;
    private ExpiStage currentStage;

    public GameScreen(PacketInputStream in) {

        gameScreen = this;

        serverTPS = Consts.SERVER_DEFAULT_TPS;

        batch = ExpiGame.get().getBatch();
        multiplexer = new InputMultiplexer();
        gameWorld = new GameWorld();

        gameStage = new GameStage();
        invStage = new InventoryStage();
        doubleInvStage = new DoubleInventoryStage();
        showGameStage();

        multiplexer.addProcessor(gameStage);
        multiplexer.addProcessor(invStage);
        multiplexer.addProcessor(doubleInvStage);
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

        // sky
        //Gdx.gl.glClearColor(0.6f, 0.8f, 1, 1);
        Color sky = getSkyColor();
        Gdx.gl.glClearColor(sky.r, sky.g, sky.b, sky.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        int parallaxWidth = 1600;
        int parallaxWidth2 = 1400;
        int parallaxWidth3 = 1000;
        int parallaxHeight = 2000;

        float xShift1 = (gameWorld.getPlayer().getLocation().x*2) % parallaxWidth;
        float yShift1 = gameWorld.getPlayer().getLocation().y*4;
        float xShift2 = (gameWorld.getPlayer().getLocation().x*6) % parallaxWidth2;
        float yShift2 = gameWorld.getPlayer().getLocation().y*6;
        float xShift3 = (gameWorld.getPlayer().getLocation().x*8) % parallaxWidth3;
        float yShift3 = gameWorld.getPlayer().getLocation().y*8;

        batch.begin();

        // parallax - needs projection matrix from gui (1000*1000)
        //batch.setColor(1f, 0.8f, 0.4f, 1);
        batch.setColor(getBGColor());
        batch.draw(Res.BG_1, -xShift1, -yShift1, parallaxWidth, parallaxHeight);
        batch.draw(Res.BG_1, parallaxWidth-xShift1, -yShift1, parallaxWidth, parallaxHeight);
        batch.draw(Res.BG_2, -xShift2, -yShift2, parallaxWidth2, parallaxHeight);
        batch.draw(Res.BG_2, parallaxWidth2-xShift2, -yShift2, parallaxWidth2, parallaxHeight);
        batch.draw(Res.BG_3, -xShift3, -yShift3, parallaxWidth3, parallaxHeight);
        batch.draw(Res.BG_3, parallaxWidth3-xShift3, -yShift3, parallaxWidth3, parallaxHeight);
        batch.setColor(Color.WHITE);

        gameWorld.render();

        if(currentStage.isDimmed()) {
            batch.setColor(0, 0, 0, 0.5f);
            batch.draw(Res.WHITE_TILE, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.setColor(1, 1, 1, 1);
        }

        batch.end();

        gameStage.draw();
        invStage.draw();
        doubleInvStage.draw();

        // lag simulator
        try {
            Thread.sleep(0);
        }catch(InterruptedException e) {
            e.printStackTrace();
        }

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
        gameWorld.getPlayer().sendTSPacket();
    }

    @Override
    public void onServerFail() {

    }

    public void showGameStage() {
        gameStage.setVisible(true);
        invStage.setVisible(false);
        doubleInvStage.setVisible(false);
        currentStage = gameStage;
    }

    public void showInvStage() {
        gameStage.setVisible(false);
        invStage.setVisible(true);
        doubleInvStage.setVisible(false);
        currentStage = invStage;
    }

    public void showDoubleInvStage() {
        gameStage.setVisible(false);
        invStage.setVisible(false);
        doubleInvStage.setVisible(true);
        currentStage = doubleInvStage;
    }

    private Color getSkyColor() {
        //todo
        return new Color(0.5f,0.5f,1,1);
    }

    private Color getBGColor() {
        //todo
        return new Color(1, 1, 1, 1);
    }

    public Color getNightColor() {
        //todo
        return new Color();
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

    public int getServerTPS() {
        return serverTPS;
    }
}
