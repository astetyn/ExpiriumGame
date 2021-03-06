package com.astetyne.expirium.client.gui.roots.game;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.resources.Res;
import com.astetyne.expirium.client.screens.GameScreen;
import com.astetyne.expirium.client.screens.MenuScreen;
import com.astetyne.expirium.client.utils.Utils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class SettingsRoot extends WidgetGroup implements GameRootable {

    public SettingsRoot(GameScreen game) {

        TextButton recipesButton = new TextButton("Cooking recipes", Res.TEXT_BUTTON_STYLE);
        recipesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setRoot(new RecipesRoot(game));
            }
        });

        Image returnButton = new Image(Res.CROSS_ICON);
        TextButton leaveButton = new TextButton("Leave game", Res.TEXT_BUTTON_STYLE);
        leaveButton.setColor(1f, 0.3f, 0.3f, 1);
        Label codeLabel = new Label("Game not paused.", Res.TITLE_LABEL_STYLE);
        codeLabel.setAlignment(Align.center);

        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setRoot(new GameRoot(game));
            }
        });

        leaveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ExpiGame.get().getClientGateway().close();
                if(ExpiGame.get().isHostingServer()) {
                    ExpiGame.get().stopServer();
                }
                ExpiGame.get().setScreen(new MenuScreen());
            }
        });

        recipesButton.setBounds(760, 180, 440, 100);
        addActor(recipesButton);
        returnButton.setBounds(1880, 890, 100, Utils.percFromW(100));
        addActor(returnButton);
        codeLabel.setBounds(760, 720, 440, 160);
        addActor(codeLabel);
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
    public void refresh() {}

    @Override
    public boolean canInteractWithWorld() {
        return false;
    }
}
