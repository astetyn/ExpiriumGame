package com.astetyne.expirium.main.gui.roots;

import com.astetyne.expirium.main.ExpiGame;
import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.screens.GameScreen;
import com.astetyne.expirium.main.screens.LauncherScreen;
import com.astetyne.expirium.main.utils.Utils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class SettingsRoot extends WidgetGroup implements ExpiRoot {

    public SettingsRoot() {

        Image returnButton = new Image(Res.CROSS_ICON);
        TextButton leaveButton = new TextButton("Leave server", Res.TEXT_BUTTON_STYLE);

        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameScreen.get().setRoot(new GameRoot());
            }
        });

        leaveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //todo: poslat serveru leave spravu a ukoncit clienta
                //todo: co ak je spusteny server? - jemne ho ukoncit
                ExpiGame.get().setScreen(new LauncherScreen());
            }
        });
        returnButton.setBounds(1880, 890, 100, Utils.percFromW(100));
        returnButton.setDebug(true);
        setDebug(true);
        addActor(returnButton);
        leaveButton.setBounds(760, 420, 440, 160);
        addActor(leaveButton);

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
