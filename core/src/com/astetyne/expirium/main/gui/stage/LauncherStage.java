package com.astetyne.expirium.main.gui.stage;

import com.astetyne.expirium.main.ExpiGame;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class LauncherStage extends Stage {

    public LauncherStage() {
        super(new StretchViewport(1000,1000), ExpiGame.get().getBatch());
    }

}
