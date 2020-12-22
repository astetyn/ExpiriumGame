package com.astetyne.expirium.main.stages;

import com.astetyne.expirium.main.Resources;
import com.astetyne.expirium.server.backend.IncomingPacket;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.List;

public abstract class ExpiStage {

    protected final Stage stage;
    protected final SpriteBatch batch;
    protected InputMultiplexer multiplexer;

     public ExpiStage() {
         batch = new SpriteBatch();
         stage = new Stage(new ScreenViewport(), batch);
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
        Resources.dispose();
    }

    public abstract void onServerUpdate(List<IncomingPacket> actions);

    public abstract void onServerFail();

    public InputMultiplexer getMultiplexer() {
        return multiplexer;
    }

    public SpriteBatch getBatch() {
        return batch;
    }
}
