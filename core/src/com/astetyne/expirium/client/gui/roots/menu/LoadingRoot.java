package com.astetyne.expirium.client.gui.roots.menu;

import com.astetyne.expirium.client.resources.Res;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public class LoadingRoot extends Table implements MenuRootable {

    private final Label label;

    public LoadingRoot(String text) {

        label = new Label(text, Res.LABEL_STYLE);
        label.setAlignment(Align.center);

        add(label).width(500);
    }

    @Override
    public Actor getActor() {
        return this;
    }

    @Override
    public void onEnd() {}
}
