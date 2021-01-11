package com.astetyne.expirium.main.screens;

import com.astetyne.expirium.main.ExpiGame;
import com.astetyne.expirium.main.InventoryHandler;
import com.astetyne.expirium.main.PlayerDataHandler;
import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.gui.roots.ExpiRoot;
import com.astetyne.expirium.main.gui.roots.GameRoot;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.main.world.GameWorld;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class GameScreen implements Screen {

    private static GameScreen gameScreen;

    private final SpriteBatch batch;
    private final InputMultiplexer multiplexer;
    private final Stage stage;
    private final GameWorld gameWorld;
    private int serverTime;
    private int serverTPS;
    private final InventoryHandler inventoryHandler;
    private final PlayerDataHandler playerDataHandler;
    private ExpiRoot activeRoot;

    public GameScreen(PacketInputStream in) {

        gameScreen = this;

        serverTPS = Consts.SERVER_DEFAULT_TPS;

        batch = ExpiGame.get().getBatch();
        multiplexer = new InputMultiplexer();
        gameWorld = new GameWorld();

        inventoryHandler = new InventoryHandler();
        playerDataHandler = new PlayerDataHandler();

        stage = new Stage(new StretchViewport(2000, 1000), ExpiGame.get().getBatch());
        setRoot(new GameRoot());

        multiplexer.addProcessor(stage);

        // load init data from server
        gameWorld.loadData(in);
    }

    public void update() {

        stage.act();
        gameWorld.update();

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {

        update();

        // sky
        //Gdx.gl.glClearColor(0.6f, 0.8f, 1, 1);
        Color sky = getSkyColor();
        Gdx.gl.glClearColor(sky.r, sky.g, sky.b, sky.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        int parallaxWidth = 3200;
        int parallaxWidth2 = 2800;
        int parallaxWidth3 = 2000;
        int parallaxHeight = 1300;

        float xShift1 = (gameWorld.getPlayer().getLocation().x*2) % parallaxWidth;
        float yShift1 = gameWorld.getPlayer().getLocation().y*4;
        float xShift2 = (gameWorld.getPlayer().getLocation().x*6) % parallaxWidth2;
        float yShift2 = gameWorld.getPlayer().getLocation().y*7;
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

        if(false) {
            batch.setColor(0, 0, 0, 0.5f);
            batch.draw(Res.WHITE_TILE, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.setColor(1, 1, 1, 1);
        }

        batch.end();

        stage.draw();

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

    public void setRoot(ExpiRoot root) {
        root.getActor().setBounds(0, 0, 2000, 1000);
        stage.clear();
        stage.addActor(root.getActor());
        activeRoot = root;
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

    public int getServerTPS() {
        return serverTPS;
    }

    public InventoryHandler getInventoryHandler() {
        return inventoryHandler;
    }

    public PlayerDataHandler getPlayerDataHandler() {
        return playerDataHandler;
    }

    public ExpiRoot getActiveRoot() {
        return activeRoot;
    }
}
