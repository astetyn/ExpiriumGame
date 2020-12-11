package com.astetyne.main.stages;

import com.astetyne.main.net.server.actions.ServerAction;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.List;

public abstract class Stage {

    protected final SpriteBatch batch;

     public Stage() {
         batch = new SpriteBatch();
     }

    public abstract void update();

    public abstract void render();

    public abstract void resize();

    public void dispose() {
        batch.dispose();
    }

    public abstract void onServerUpdate(List<ServerAction> actions);

}
