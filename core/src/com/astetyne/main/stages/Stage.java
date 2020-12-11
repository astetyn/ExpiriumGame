package com.astetyne.main.stages;

import com.astetyne.main.net.server.actions.ServerAction;

import java.util.List;

public abstract class Stage {

    public abstract void update();

    public abstract void render();

    public abstract void resize();

    public abstract void dispose();

    public abstract void onServerUpdate(List<ServerAction> actions);

}
