package com.astetyne.expirium.main.screens;

import com.astetyne.expirium.main.ExpiGame;
import com.astetyne.expirium.main.gui.roots.LauncherRoot;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class LauncherScreen implements Screen {

    private static LauncherScreen launcherScreen;

    private final Stage stage;

    public LauncherScreen() {
        this("");
    }

    public LauncherScreen(String info) {

        launcherScreen = this;

        stage = new Stage(new StretchViewport(2000, 1000), ExpiGame.get().getBatch());
        setRoot(new LauncherRoot(info));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
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
        Gdx.input.setInputProcessor(null);
        dispose();
        launcherScreen = null;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public static LauncherScreen get() {
        return launcherScreen;
    }

    public void setRoot(Actor root) {
        root.setBounds(0, 0, 2000, 1000);
        stage.clear();
        stage.addActor(root);
    }
}
