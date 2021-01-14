package com.astetyne.expirium.client.gui.roots;

import com.astetyne.expirium.client.Res;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class TestRoot extends WidgetGroup implements ExpiRoot {

    public TestRoot() {
        Actor a = new Image(Res.BG_3);
        a.setBounds(100, 100, 100, 100);
        addActor(a);

    }

    @Override
    public Actor getActor() {
        return this;
    }

    @Override
    public boolean isDimmed() {
        return false;
    }

    @Override
    public void refresh() {

    }

    @Override
    public boolean canInteractWithWorld() {
        return true;
    }
}
