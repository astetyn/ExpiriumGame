package com.astetyne.expirium.main.stages;

import com.astetyne.expirium.main.ExpiriumGame;
import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.gui.GUILayout;
import com.astetyne.expirium.main.gui.GameGUILayout;
import com.astetyne.expirium.main.items.inventory.Inventory;
import com.astetyne.expirium.main.utils.Constants;
import com.astetyne.expirium.main.world.GameWorld;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

public class GameStage extends ExpiStage {

    private static GameStage game;

    private GameWorld gameWorld;
    private final Inventory inventory;
    private final Box2DDebugRenderer b2dr;
    private final GameGUILayout gameGuiLayout;
    private GUILayout guiLayout;

    public GameStage() {

        game = this;

        b2dr = new Box2DDebugRenderer();
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

        Gdx.gl.glClearColor(0.6f, 0.8f, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        gameWorld.render();
        batch.end();

        stage.draw();

        if(Constants.DEBUG) b2dr.render(gameWorld.getB2dWorld(), gameWorld.getCamera().combined);

    }

    @Override
    public void resize() {

        if(gameWorld != null) gameWorld.resize();
        Res.ARIAL_FONT.getData().setScale((float)Gdx.graphics.getHeight() / Gdx.graphics.getWidth(), 1);
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
}
