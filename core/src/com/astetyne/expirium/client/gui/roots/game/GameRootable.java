package com.astetyne.expirium.client.gui.roots.game;

import com.badlogic.gdx.scenes.scene2d.Actor;

public interface GameRootable {

    Actor getActor();

    boolean isDimmed();

    void refresh();

    boolean canInteractWithWorld();

}
