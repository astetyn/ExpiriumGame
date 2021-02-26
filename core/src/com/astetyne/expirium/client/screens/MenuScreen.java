package com.astetyne.expirium.client.screens;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.Res;
import com.astetyne.expirium.client.gui.roots.menu.MainMenuRoot;
import com.astetyne.expirium.client.gui.roots.menu.MenuRootable;
import com.astetyne.expirium.client.gui.widget.WarnMsgLabel;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class MenuScreen implements Screen {

    private final Stage stage;
    private final WarnMsgLabel warnMsgLabel;
    private MenuRootable activeRoot;

    public MenuScreen() {
        this("");
    }

    public MenuScreen(String info) {

        stage = new Stage(new StretchViewport(2000, 1000), ExpiGame.get().getBatch());

        warnMsgLabel = new WarnMsgLabel(Res.TITLE_LABEL_STYLE);
        warnMsgLabel.setBounds(0, 700, 2000, 200);
        stage.addActor(warnMsgLabel);

        setRoot(new MainMenuRoot(info, this));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        System.out.println("Showing launcher screen.");
    }

    @Override
    public void render(float delta) {

        stage.act();

        Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        if(activeRoot != null) activeRoot.onEnd();
        System.out.println("Hiding launcher screen.");
        Gdx.input.setInputProcessor(null);
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void setRoot(MenuRootable root) {
        if(activeRoot != null) activeRoot.onEnd();
        root.getActor().setBounds(0, 0, 2000, 1000);
        stage.clear();
        stage.addActor(root.getActor());
        stage.addActor(warnMsgLabel);
        activeRoot = root;
    }

    public void addWarning(String msg, int duration, Color color) {
        warnMsgLabel.setMsg(msg, duration, color);
    }

    public Stage getStage() {
        return stage;
    }
}
