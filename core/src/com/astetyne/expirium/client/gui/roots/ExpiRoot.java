package com.astetyne.expirium.client.gui.roots;

import com.badlogic.gdx.scenes.scene2d.Actor;

public interface ExpiRoot {

    Actor getActor();

    boolean isDimmed();

    void refresh();

    boolean canInteractWithWorld();

}
