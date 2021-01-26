package com.astetyne.expirium.client.gui.roots.menu;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.Res;
import com.astetyne.expirium.client.screens.MenuScreen;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.Utils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class MainMenuRoot extends WidgetGroup {

    private final Label infoLabel;

    public MainMenuRoot(String info, MenuScreen menu) {

        if(Consts.DEBUG) setDebug(true);
        if(Consts.DEBUG) ExpiGame.get().setPlayerName("palko");

        // title
        Label title = new Label("Expirium", Res.TITLE_LABEL_STYLE);
        title.setAlignment(Align.center);

        // version label
        Label versionLabel = new Label(ExpiGame.version, Res.LABEL_STYLE);
        versionLabel.setColor(Color.RED);

        // name text field
        TextField nameTF = new TextField(ExpiGame.get().getPlayerName(), Res.TEXT_FIELD_STYLE);
        nameTF.setMessageText("Your nickname");
        nameTF.setAlignment(Align.center);
        nameTF.setTextFieldFilter((textField1, c) -> Character.toString(c).matches("^[0-9a-zA-Z_+\\- ]"));
        nameTF.setMaxLength(16);
        nameTF.addListener(new InputListener() {
            @Override
            public boolean keyTyped (InputEvent event, char character) {
                ExpiGame.get().setPlayerName(nameTF.getText().trim());
                return false;
            }
        });

        // join button
        TextButton joinButton = new TextButton("Join a game.", Res.TEXT_BUTTON_STYLE);
        joinButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(nameTF.getText().trim().isEmpty()) {
                    setInfo("You must enter your nickname.", Color.RED);
                    return;
                }
                menu.setRoot(new ServerListRoot(menu));
            }
        });

        // host button
        TextButton hostButton = new TextButton("Host a game.", Res.TEXT_BUTTON_STYLE);
        hostButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(nameTF.getText().isEmpty()) {
                    setInfo("You must enter your nickname.", Color.RED);
                    return;
                }
                menu.setRoot(new HostCreatorRoot(menu));
            }
        });

        // info label
        infoLabel = new Label(info, Res.LABEL_STYLE);
        infoLabel.setAlignment(Align.center);
        infoLabel.setColor(Color.RED);

        // author label
        Label authorLabel = new Label("- Created by astetyne -", Res.LABEL_STYLE);
        authorLabel.setAlignment(Align.center);
        authorLabel.setColor(Color.DARK_GRAY);

        versionLabel.setBounds(30, 930, 200, 50);
        addActor(versionLabel);

        float textWidth = Utils.getTextWidth(title.getText().toString(), Res.MAIN_FONT);
        title.setBounds(1000 - textWidth/2, 800, textWidth, 200);
        addActor(title);

        nameTF.setBounds(670, 600, 660, 110);
        addActor(nameTF);

        joinButton.setBounds(670, 370, 660, 110);
        addActor(joinButton);

        hostButton.setBounds(670, 250, 660, 110);
        addActor(hostButton);

        infoLabel.setBounds(670, 160, 660, 110);
        addActor(infoLabel);

        authorLabel.setBounds(670, 50, 660, 110);
        addActor(authorLabel);

    }

    public void setInfo(String info, Color color) {
        infoLabel.setColor(color);
        infoLabel.setText(info);
    }

}
