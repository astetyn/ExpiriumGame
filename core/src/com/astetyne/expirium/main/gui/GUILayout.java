package com.astetyne.expirium.main.gui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public abstract class GUILayout {

    public abstract void update();

    public abstract Table getRootTable();

    public abstract void build(Stage stage);

}
