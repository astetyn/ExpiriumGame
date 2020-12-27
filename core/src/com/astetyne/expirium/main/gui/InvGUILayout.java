package com.astetyne.expirium.main.gui;

import com.astetyne.expirium.main.Resources;
import com.astetyne.expirium.main.stages.GameStage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class InvGUILayout extends GUILayout {

    private final Table rootTable;
    private final ImageButton returnButton;

    public InvGUILayout() {

        returnButton = new ImageButton(new TextureRegionDrawable(Resources.WHITE_TILE));
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameStage.get().setActiveGuiLayout(GameStage.get().getGameGuiLayout());
            }
        });

        this.rootTable = new Table();
        rootTable.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        rootTable.add(returnButton);

        rootTable.setDebug(true);
    }

    @Override
    public void update() {

    }

    @Override
    public Table getRootTable() {
        return rootTable;
    }

    @Override
    public void build(Stage stage) {
        stage.clear();
        stage.addActor(rootTable);
    }
}
