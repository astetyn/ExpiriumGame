package com.astetyne.expirium.client.screens;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.Res;
import com.astetyne.expirium.client.data.PlayerDataHandler;
import com.astetyne.expirium.client.gui.roots.game.DoubleInventoryRoot;
import com.astetyne.expirium.client.gui.roots.game.GameRoot;
import com.astetyne.expirium.client.gui.roots.game.GameRootable;
import com.astetyne.expirium.client.gui.widget.WarnMsgLabel;
import com.astetyne.expirium.client.resources.TileTex;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.world.Background;
import com.astetyne.expirium.client.world.GameWorld;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.astetyne.expirium.server.net.SimpleServerPacket;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
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
    private final Background background;
    private float dayTime;
    private final int serverTPS;
    private final PlayerDataHandler playerDataHandler;
    private GameRootable activeRoot;
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

        setRoot(new com.astetyne.expirium.client.gui.roots.game.GameRoot());

        multiplexer.addProcessor(stage);

        gameWorld = new GameWorld();

        // load init data from server
        gameWorld.loadData(in);

        background = new Background(gameWorld);
    }

    public void update() {

        stage.act();
        gameWorld.update();

    }

    @Override
    public void show() {
        System.out.println("Showing game screen.");
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {

        update();

        batch.begin();

        background.draw(batch, dayTime);

        gameWorld.draw(batch);

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

        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            gameWorld.getPlayer().onHandPunch();
        }

    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        System.out.println("Hiding launcher screen.");
        dispose();
        gameScreen = null;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void setRoot(GameRootable root) {
        root.getActor().setBounds(0, 0, 2000, 1000);
        stage.clear();
        stage.addActor(root.getActor());
        stage.addActor(warnMsgLabel);
        activeRoot = root;
    }

    public void onSimplePacket(SimpleServerPacket ssp) {
        if(ssp == SimpleServerPacket.CLOSE_DOUBLE_INV) {
            if(getActiveRoot() instanceof DoubleInventoryRoot) {
                setRoot(new GameRoot());
            }
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

    public GameRootable getActiveRoot() {
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
