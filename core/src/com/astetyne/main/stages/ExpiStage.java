package com.astetyne.main.stages;

import com.astetyne.main.net.server.actions.ServerAction;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.List;

public abstract class ExpiStage {

    protected final Stage stage;
    protected final SpriteBatch batch;

     public ExpiStage() {
         batch = new SpriteBatch();
         stage = new Stage(new ScreenViewport());
         Gdx.input.setInputProcessor(stage);
     }

    public abstract void update();

    public abstract void render();

    public abstract void resize();

    public void dispose() {
        batch.dispose();
        stage.dispose();
    }

    public abstract void onServerUpdate(List<ServerAction> actions);

}
