package com.astetyne.expirium.main.stages;

import com.astetyne.expirium.main.Res;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public abstract class ExpiStage {

    protected final Stage stage;
    protected final SpriteBatch batch;
    protected InputMultiplexer multiplexer;

     public ExpiStage() {
         batch = new SpriteBatch();
         stage = new Stage(new StretchViewport(1000,1000), batch);
         multiplexer = new InputMultiplexer();
         multiplexer.addProcessor(stage);
         Gdx.input.setInputProcessor(multiplexer);
     }

    public abstract void update();

    public abstract void render();

    public abstract void resize();

    public void dispose() {
        batch.dispose();
        stage.dispose();
        Res.dispose();
    }

    public abstract void onServerUpdate();

    public abstract void onServerFail();

    public InputMultiplexer getMultiplexer() {
        return multiplexer;
    }

    public SpriteBatch getBatch() {
        return batch;
    }
}