package com.astetyne.expirium.main.gui.roots;

import com.badlogic.gdx.scenes.scene2d.Actor;

public interface ExpiRoot {

    Actor getActor();

    boolean isDimmed();

    void refresh();

    //todo canInteractWithWorld() ? - pretoze to tileplacer potrebuje

}
