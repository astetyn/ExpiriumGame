package com.astetyne.expirium.main.gui.roots;

import com.astetyne.expirium.main.Res;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public class LoadingRoot extends Table {

    private final Label label;

    public LoadingRoot(String text) {

        label = new Label(text, Res.LABEL_STYLE);
        label.setAlignment(Align.center);

        add(label).width(500);
    }
}
