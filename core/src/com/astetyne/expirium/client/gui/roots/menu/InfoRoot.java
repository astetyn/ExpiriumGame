package com.astetyne.expirium.client.gui.roots.menu;

import com.astetyne.expirium.client.resources.Res;
import com.astetyne.expirium.client.screens.MenuScreen;
import com.astetyne.expirium.client.utils.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class InfoRoot extends WidgetGroup implements MenuRootable {

    public InfoRoot(MenuScreen menu) {

        String text = "Expirium is my small open source project. Goal of this game is to stay " +
                "alive for as long as possible. Even more fun is to play with your friend but " +
                "you must be connected on the same router/hotspot. Game still contains minor " +
                "bugs so do not be angry if your world will be unintentionally erased. Hope you " +
                "enjoy my small game! You can find source code on https://github.com/astetyn/ExpiriumGame";

        Label textLabel = new Label(text, Res.LABEL_STYLE);
        textLabel.setAlignment(Align.center);
        textLabel.setWrap(true);

        Image returnButton = new Image(Res.CROSS_ICON);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                menu.setRoot(new MainMenuRoot("", menu));
            }
        });

        textLabel.setBounds(200, 100, 1600, 800);
        addActor(textLabel);

        returnButton.setBounds(1880, 890, 100, Utils.percFromW(100));
        addActor(returnButton);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Gdx.gl.glClearColor(0.6f, 0.6f, 0.6f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.draw(batch, parentAlpha);
    }

    @Override
    public Actor getActor() {
        return this;
    }

    @Override
    public void onEnd() {

    }
}
