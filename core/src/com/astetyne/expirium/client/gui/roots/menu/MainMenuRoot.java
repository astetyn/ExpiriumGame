package com.astetyne.expirium.client.gui.roots.menu;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.Res;
import com.astetyne.expirium.client.screens.MenuScreen;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import java.net.UnknownHostException;

public class MainMenuRoot extends Table {

    private final TextField nameTF, codeTF;
    private final TextButton joinButton, hostServerButton;
    private final Label title, infoLabel, creditsLabel;

    public MainMenuRoot(String info, MenuScreen menu) {

        if(Consts.DEBUG) setDebug(true);
        if(Consts.DEBUG) ExpiGame.get().setPlayerName("palko");

        Label versionLabel = new Label(ExpiGame.version, Res.LABEL_STYLE);
        versionLabel.setColor(Color.RED);

        nameTF = new TextField(ExpiGame.get().getPlayerName(), Res.TEXT_FIELD_STYLE);
        nameTF.setMessageText("Enter name");
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
        codeTF = new TextField("", Res.TEXT_FIELD_STYLE);
        codeTF.setMessageText("Enter game code");
        codeTF.setAlignment(Align.center);
        codeTF.setTextFieldFilter((textField1, c) -> Character.toString(c).matches("^[0-9a-zA-Z]"));
        codeTF.setMaxLength(6);

        codeTF.setOnscreenKeyboard(visible -> {
            codeTF.setDisabled(true);
            Gdx.input.setOnscreenKeyboardVisible(true);
            Gdx.input.getTextInput(new Input.TextInputListener() {
                @Override
                public void input(String text) {
                    codeTF.setText(text);
                    codeTF.setDisabled(false);
                }

                @Override
                public void canceled() {
                    System.out.println("Cancelled.");
                }
            }, "Enter game code", "", "game code");
        });

        joinButton = new TextButton("Join!", Res.TEXT_BUTTON_STYLE);
        hostServerButton = new TextButton("Host a game.", Res.TEXT_BUTTON_STYLE);

        title = new Label("Expirium", Res.TITLE_LABEL_STYLE);
        title.setAlignment(Align.center);

        infoLabel = new Label(info, Res.LABEL_STYLE);
        infoLabel.setAlignment(Align.center);
        infoLabel.setColor(Color.RED);

        creditsLabel = new Label("- Created by astetyne -", Res.LABEL_STYLE);
        creditsLabel.setAlignment(Align.center);
        creditsLabel.setColor(Color.DARK_GRAY);

        joinButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(nameTF.getText().trim().isEmpty()) {
                    setInfo("You must enter your nickname.", Color.RED);
                    return;
                }
                menu.setRoot(new LoadingRoot("Connecting to: "+codeTF.getText()+" ..."));
                try {
                    ExpiGame.get().startClient(Utils.getAddressFromCode(codeTF.getText()));
                }catch(UnknownHostException e) {
                    setInfo("Wrong code. Are you on the same network?", Color.RED);
                }
            }
        });

        hostServerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(nameTF.getText().isEmpty()) {
                    setInfo("You must enter your nickname.", Color.RED);
                    return;
                }
                menu.setRoot(new HostCreatorRoot(menu));
            }
        });

        Table upperTable = new Table();
        upperTable.add(versionLabel).padLeft(10).padTop(10).align(Align.topLeft).growX();
        upperTable.row();
        upperTable.add(title).center();
        if(Consts.DEBUG) upperTable.setDebug(true);

        add(upperTable).growX();
        row();
        add(nameTF).width(1000).height(100).padBottom(10).padTop(40);
        row();
        add(codeTF).width(1000).height(100).padBottom(10).padTop(100);
        row();
        add(joinButton).width(1000).height(100);
        row();
        add(infoLabel).width(1000);
        row();
        add(hostServerButton).width(1000).expandY().height(100);
        row();
        add(creditsLabel).padBottom(5);

    }

    public void setInfo(String info, Color color) {
        infoLabel.setColor(color);
        infoLabel.setText(info);
    }

}
