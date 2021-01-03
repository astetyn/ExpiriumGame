package com.astetyne.expirium.main.stages;

import com.astetyne.expirium.main.ExpiriumGame;
import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.gui.GUILayout;
import com.astetyne.expirium.main.gui.GameGUILayout;
import com.astetyne.expirium.main.items.inventory.Inventory;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.main.world.GameWorld;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

public class GameStage extends ExpiStage {

    private static GameStage game;

    private GameWorld gameWorld;
    private final Inventory inventory;
    private final Box2DDebugRenderer b2dr;
    private GLProfiler profiler;
    private final GameGUILayout gameGuiLayout;
    private GUILayout guiLayout;
    private int serverTime;

    public GameStage() {

        game = this;

        b2dr = new Box2DDebugRenderer();
        profiler = new GLProfiler(Gdx.graphics);
        profiler.enable();
        System.out.println("Display density: "+Gdx.graphics.getDensity());
        inventory = new Inventory();
        resize();

        gameGuiLayout = new GameGUILayout();

    }

    @Override
    public void update() {

        stage.act();
        gameWorld.update();
        guiLayout.update();

    }

    @Override
    public void render() {

        profiler.reset();

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

        batch.end();

        stage.draw();

        if(Consts.DEBUG) b2dr.render(gameWorld.getB2dWorld(), gameWorld.getCamera().combined);

        //System.out.println("draw calls: "+profiler.getDrawCalls()+"\ntex bindings: "+profiler.getTextureBindings());

    }

    @Override
    public void resize() {

        if(gameWorld != null) gameWorld.resize();
        Res.MAIN_FONT.getData().setScale((float)Gdx.graphics.getHeight() / Gdx.graphics.getWidth(), 1);
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        if(getGuiLayout() != null) getGuiLayout().resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    }

    @Override
    public void dispose() {
        super.dispose();
        gameWorld.dispose();
    }

    public void initGameStage() {
        int invID = ExpiriumGame.get().getClientGateway().getIn().getInt();
        inventory.getInventoryGrid().setId(invID);
        int numberOfChunks = ExpiriumGame.get().getClientGateway().getIn().getInt();
        gameWorld = new GameWorld(numberOfChunks);
        gameWorld.postSetup();
        setActiveGuiLayout(gameGuiLayout);
    }

    public void setActiveGuiLayout(GUILayout layout) {
        guiLayout = layout;
        guiLayout.build(stage);
    }

    @Override
    public void onServerUpdate() {
        gameWorld.getPlayer().generateMovePacket();
    }

    @Override
    public void onServerFail() {

    }

    public GUILayout getGuiLayout() {
        return guiLayout;
    }

    public Inventory getInv() {
        return inventory;
    }

    public GameWorld getWorld() {
        return gameWorld;
    }

    public static GameStage get() {
        return game;
    }

    public GameGUILayout getGameGuiLayout() {
        return gameGuiLayout;
    }

    public int getServerTime() {
        return serverTime;
    }

    public void setServerTime(int serverTime) {
        this.serverTime = serverTime;
    }
}
