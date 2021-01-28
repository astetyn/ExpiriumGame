package com.astetyne.expirium.client.gui.roots.game;

import com.astetyne.expirium.client.Res;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class DeathRoot extends WidgetGroup implements GameRootable {

    public DeathRoot() {

        Label deathLabel = new Label("You died", Res.LABEL_STYLE);

        deathLabel.setBounds(500, 700, 1000, 200);
        addActor(deathLabel);
    }

    @Override
    public Actor getActor() {
        return this;
    }

    @Override
    public boolean isDimmed() {
        return true;
    }

    @Override
    public void refresh() {

    }

    @Override
    public boolean canInteractWithWorld() {
        return false;
    }
}
