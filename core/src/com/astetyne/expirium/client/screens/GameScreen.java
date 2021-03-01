package com.astetyne.expirium.client.screens;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.Res;
import com.astetyne.expirium.client.data.PlayerDataHandler;
import com.astetyne.expirium.client.gui.roots.game.DoubleInventoryRoot;
import com.astetyne.expirium.client.gui.roots.game.GameRoot;
import com.astetyne.expirium.client.gui.roots.game.GameRootable;
import com.astetyne.expirium.client.gui.widget.OverlapImage;
import com.astetyne.expirium.client.gui.widget.WarnMsgLabel;
import com.astetyne.expirium.client.resources.TileTex;
import com.astetyne.expirium.client.world.Background;
import com.astetyne.expirium.client.world.ClientWorld;
import com.astetyne.expirium.server.core.world.inventory.ChosenSlot;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.astetyne.expirium.server.net.SimpleServerPacket;
import com.badlogic.gdx.Gdx;
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
    private final OverlapImage overlapImage;
    private final ClientWorld world;
    private final Background background;
    private int time;
    private final PlayerDataHandler playerDataHandler;
    private GameRootable activeRoot;
    private boolean buildViewActive;

    public GameScreen(PacketInputStream in) {

        gameScreen = this;

        buildViewActive = false;

        batch = ExpiGame.get().getBatch();
        multiplexer = new InputMultiplexer();

        playerDataHandler = new PlayerDataHandler();

        stage = new Stage(new StretchViewport(2000, 1000), ExpiGame.get().getBatch());

        warnMsgLabel = new WarnMsgLabel(Res.WARN_LABEL_STYLE);
        warnMsgLabel.setBounds(0, 700, 2000, 200);

        multiplexer.addProcessor(stage);

        world = new ClientWorld(in);

        overlapImage = new OverlapImage(world, playerDataHandler);
        overlapImage.setBounds(0, 0, stage.getWidth(), stage.getHeight());

        background = new Background(world);

        setRoot(new GameRoot());

    }

    public void update() {

        stage.act();
        world.update();

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

        background.draw(batch, time);

        world.draw(batch);

        if(activeRoot.isDimmed()) {
            batch.setColor(0, 0, 0, 0.5f);
            batch.draw(TileTex.WHITE_TILE.getTex(), 0, 0, world.getTerrainWidth(), world.getTerrainHeight());
            batch.setColor(1, 1, 1, 1);
        }

        batch.end();

        stage.draw();

        /*try { // lag simulator
            Thread.sleep(0);
        }catch(InterruptedException e) {
            e.printStackTrace();
        }*/

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
        stage.addActor(overlapImage);
        activeRoot = root;
    }

    public void onSimplePacket(SimpleServerPacket ssp) {
        if(ssp == SimpleServerPacket.CLOSE_DOUBLE_INV) {
            if(getActiveRoot() instanceof DoubleInventoryRoot) {
                setRoot(new GameRoot());
            }
        }
    }

    public void onWarningMsgPacket(PacketInputStream in) {
        String msg = in.getString();
        int duration = in.getInt();
        Color c = in.getColor().getColor();
        addWarning(msg, duration, c);
    }

    public ClientWorld getWorld() {
        return world;
    }

    public static GameScreen get() {
        return gameScreen;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public InputMultiplexer getMultiplexer() {
        return multiplexer;
    }

    public PlayerDataHandler getPlayerData() {
        return playerDataHandler;
    }

    public GameRootable getActiveRoot() {
        return activeRoot;
    }

    public boolean isBuildViewActive() {
        return buildViewActive && playerDataHandler.getHotSlotsData().getChosenSlot() == ChosenSlot.MATERIAL_SLOT;
    }

    public void toggleBuildViewActive() {
        buildViewActive = !buildViewActive;
    }

    public void addWarning(String msg, int durationMillis, Color color) {
        warnMsgLabel.setMsg(msg, durationMillis, color);
    }

    public OverlapImage getOverlapImage() {
        return overlapImage;
    }
}
