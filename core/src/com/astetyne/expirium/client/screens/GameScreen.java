package com.astetyne.expirium.client.screens;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.Res;
import com.astetyne.expirium.client.data.PlayerDataHandler;
import com.astetyne.expirium.client.gui.roots.DoubleInventoryRoot;
import com.astetyne.expirium.client.gui.roots.ExpiRoot;
import com.astetyne.expirium.client.gui.roots.GameRoot;
import com.astetyne.expirium.client.resources.BGRes;
import com.astetyne.expirium.client.resources.TileTex;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.WarnMsgLabel;
import com.astetyne.expirium.client.world.GameWorld;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.astetyne.expirium.server.net.SimpleServerPacket;
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
    private final WarnMsgLabel warnMsgLabel;
    private final GameWorld gameWorld;
    private float dayTime;
    private final int serverTPS;
    private final PlayerDataHandler playerDataHandler;
    private ExpiRoot activeRoot;
    private boolean buildViewActive;

    public GameScreen(PacketInputStream in) {

        gameScreen = this;

        buildViewActive = false;

        serverTPS = Consts.SERVER_DEFAULT_TPS;

        batch = ExpiGame.get().getBatch();
        multiplexer = new InputMultiplexer();

        playerDataHandler = new PlayerDataHandler();

        stage = new Stage(new StretchViewport(2000, 1000), ExpiGame.get().getBatch());

        warnMsgLabel = new WarnMsgLabel(Res.LABEL_STYLE);
        warnMsgLabel.setBounds(0, 700, 2000, 200);

        setRoot(new GameRoot());

        multiplexer.addProcessor(stage);

        gameWorld = new GameWorld();

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

        float xShift1 = (gameWorld.getCamera().position.x*2) % parallaxWidth;
        float yShift1 = gameWorld.getCamera().position.y*4;
        float xShift2 = (gameWorld.getCamera().position.x*6) % parallaxWidth2;
        float yShift2 = gameWorld.getCamera().position.y*7;
        float xShift3 = (gameWorld.getCamera().position.x*8) % parallaxWidth3;
        float yShift3 = gameWorld.getCamera().position.y*8;

        batch.begin();

        // parallax - needs projection matrix from gui (2000*1000)
        batch.setColor(getBGColor());
        BGRes.BACKGROUND_1.getDrawable().draw(batch, -xShift1, -yShift1, parallaxWidth, parallaxHeight);
        BGRes.BACKGROUND_1.getDrawable().draw(batch, parallaxWidth-xShift1, -yShift1, parallaxWidth, parallaxHeight);
        BGRes.BACKGROUND_2.getDrawable().draw(batch, -xShift2, -yShift2, parallaxWidth2, parallaxHeight);
        BGRes.BACKGROUND_2.getDrawable().draw(batch, parallaxWidth2-xShift2, -yShift2, parallaxWidth2, parallaxHeight);
        BGRes.BACKGROUND_3.getDrawable().draw(batch, -xShift3, -yShift3, parallaxWidth3, parallaxHeight);
        BGRes.BACKGROUND_3.getDrawable().draw(batch, parallaxWidth3-xShift3, -yShift3, parallaxWidth3, parallaxHeight);
        batch.setColor(Color.WHITE);

        gameWorld.render();

        if(activeRoot.isDimmed()) {
            batch.setColor(0, 0, 0, 0.5f);
            batch.draw(TileTex.WHITE_TILE.getTex(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
        dispose();
        gameScreen = null;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void setRoot(ExpiRoot root) {
        root.getActor().setBounds(0, 0, 2000, 1000);
        stage.clear();
        stage.addActor(root.getActor());
        stage.addActor(warnMsgLabel);
        activeRoot = root;
    }

    private Color getSkyColor() {

        float moonLight = 0f;

        if(dayTime >= 0 && dayTime < 25) { // sunrise
            float b = Math.max(1f / 25 * dayTime, moonLight);
            return new Color(b, b, b, 1);
        }else if(dayTime >= 25 && dayTime < 600) { // day
            return new Color(1, 1, 1, 1);
        }else if(dayTime >= 600 && dayTime < 625) { // dawn
            float b = Math.max(1f / 25 * (625 - dayTime), moonLight);
            return new Color(b, b, b, 1);
        }else { // night
            return new Color(moonLight, moonLight, moonLight, 1);
        }
    }

    private Color getBGColor() {

        float moonLight = 0.15f;

        if(dayTime >= 0 && dayTime < 25) { // sunrise
            float b = Math.max(1f / 25 * dayTime, moonLight);
            return new Color(b, b, b, 1);
        }else if(dayTime >= 25 && dayTime < 600) { // day
            return new Color(1, 1, 1, 1);
        }else if(dayTime >= 600 && dayTime < 625) { // dawn
            float b = Math.max(1f / 25 * (625 - dayTime), moonLight);
            return new Color(b, b, b, 1);
        }else { // night
            return new Color(moonLight, moonLight, moonLight, 1);
        }
    }

    public void onSimplePacket(SimpleServerPacket ssp) {
        if(ssp == SimpleServerPacket.CLOSE_DOUBLE_INV) {
            if(getActiveRoot() instanceof DoubleInventoryRoot) {
                setRoot(new GameRoot());
            }
        }else if(ssp == SimpleServerPacket.DEATH_EVENT) {
            addWarning("You died", 2, Color.RED);
        }
    }

    public GameWorld getWorld() {
        return gameWorld;
    }

    public static GameScreen get() {
        return gameScreen;
    }

    public float getDayTime() {
        return dayTime;
    }

    public void setDayTime(float dayTime) {
        this.dayTime = dayTime;
    }

    public InputMultiplexer getMultiplexer() {
        return multiplexer;
    }

    public int getServerTPS() {
        return serverTPS;
    }

    public PlayerDataHandler getPlayerData() {
        return playerDataHandler;
    }

    public ExpiRoot getActiveRoot() {
        return activeRoot;
    }

    public boolean isBuildViewActive() {
        return buildViewActive;
    }

    public void toggleBuildViewActive() {
        buildViewActive = !buildViewActive;
    }

    public void addWarning(String msg, long duration, Color color) {
        warnMsgLabel.addWarning(msg, duration, color);
    }
}
