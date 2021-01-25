package com.astetyne.expirium.client.screens;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.Res;
import com.astetyne.expirium.client.gui.roots.menu.MainMenuRoot;
import com.astetyne.expirium.client.utils.WarnMsgLabel;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class MenuScreen implements Screen {

    private final Stage stage;
    private final WarnMsgLabel warnMsgLabel;

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
        System.out.println("Hiding launcher screen.");
        Gdx.input.setInputProcessor(null);
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void setRoot(Actor root) {
        root.setBounds(0, 0, 2000, 1000);
        stage.clear();
        stage.addActor(root);
        stage.addActor(warnMsgLabel);
    }

    public void addWarning(String msg, long duration, Color color) {
        warnMsgLabel.addWarning(msg, duration, color);
    }
}
