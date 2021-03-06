package com.astetyne.expirium.client.gui.roots.menu;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.gui.widget.TextInputRoot;
import com.astetyne.expirium.client.resources.GuiRes;
import com.astetyne.expirium.client.resources.PlayerCharacter;
import com.astetyne.expirium.client.resources.Res;
import com.astetyne.expirium.client.screens.MenuScreen;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import java.io.*;

public class MainMenuRoot extends WidgetGroup implements MenuRootable {

    private static final String path = "userdata";

    private final Label infoLabel;

    public MainMenuRoot(String info, MenuScreen menu) {

        if(Consts.DEBUG) setDebug(true);

        MainMenuRoot ref = this;

        loadData();

        // info button
        Image infoButton = new Image(GuiRes.SETTINGS_ICON.getDrawable());
        infoButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                menu.setRoot(new InfoRoot(menu));
            }
        });

        // title
        Label title = new Label("Expirium", Res.TITLE_LABEL_STYLE);
        title.setAlignment(Align.center);

        // version label
        Label versionLabel = new Label(Consts.VERSION_TEXT, Res.LABEL_STYLE);
        versionLabel.setColor(Color.RED);

        // name text field
        TextField nameTF = new TextField(ExpiGame.get().getPlayerName(), Res.TEXT_FIELD_STYLE);
        nameTF.setMessageText("Your nickname");
        nameTF.setAlignment(Align.center);
        nameTF.setTextFieldFilter((textField1, c) -> Character.toString(c).matches("^[0-9a-zA-Z_+\\- ]"));
        nameTF.setMaxLength(16);

        nameTF.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                TextInputRoot tir = new TextInputRoot(() -> {
                    menu.setRoot(ref);
                    ExpiGame.get().setPlayerName(nameTF.getText());
                }, nameTF);
                menu.setRoot(tir);
                tir.setFocus(menu.getStage());
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
        TextButton hostButton = new TextButton("Create a game.", Res.TEXT_BUTTON_STYLE);
        hostButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(nameTF.getText().isEmpty()) {
                    setInfo("You must enter your nickname.", Color.RED);
                    return;
                }
                menu.setRoot(new WorldManagerRoot(menu));
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

        // character choose
        PlayerCharacter maleChar = PlayerCharacter.FENDER;
        PlayerCharacter femaleChar = PlayerCharacter.AMANDA;
        Image maleImg = new Image(maleChar.getThumbnail());
        Image femaleImg = new Image(femaleChar.getThumbnail());
        femaleImg.setColor(0.5f, 0.5f, 0.5f, 0.5f);
        maleImg.setColor(0.5f, 0.5f, 0.5f, 0.5f);
        if(ExpiGame.get().getCharacter() == maleChar) {
            maleImg.setColor(Color.WHITE);
        }else if(ExpiGame.get().getCharacter() == femaleChar) {
            femaleImg.setColor(Color.WHITE);
        }

        maleImg.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(ExpiGame.get().getCharacter() != maleChar) {
                    maleImg.setColor(Color.WHITE);
                    femaleImg.setColor(0.5f, 0.5f, 0.5f, 0.5f);
                    ExpiGame.get().setCharacter(PlayerCharacter.FENDER);
                }
            }
        });

        femaleImg.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(ExpiGame.get().getCharacter() != femaleChar) {
                    femaleImg.setColor(Color.WHITE);
                    maleImg.setColor(0.5f, 0.5f, 0.5f, 0.5f);
                    ExpiGame.get().setCharacter(PlayerCharacter.AMANDA);
                }
            }
        });

        infoButton.setBounds(30, 870, 100, Utils.percFromW(100));
        addActor(infoButton);

        versionLabel.setBounds(1750, 930, 200, 50);
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

        float thumbnailWidth = 150;
        float thumbnailHeight = Utils.percFromW(thumbnailWidth);
        maleImg.setBounds(20, 10, thumbnailWidth, thumbnailHeight);
        femaleImg.setBounds(20 + thumbnailWidth + 20, 10, thumbnailWidth, thumbnailHeight);
        addActor(maleImg);
        addActor(femaleImg);

    }

    public void setInfo(String info, Color color) {
        infoLabel.setColor(color);
        infoLabel.setText(info);
    }

    @Override
    public Actor getActor() {
        return this;
    }

    @Override
    public void onEnd() {
        saveData();
    }

    private void loadData() {

        FileHandle file = Gdx.files.local(path);
        if(!file.exists()) return;
        try {
            DataInputStream in = new DataInputStream(new BufferedInputStream(file.read()));

            StringBuilder sb = new StringBuilder();

            int len = in.readInt();
            for(int i = 0; i < len; i++) {
                sb.append(in.readChar());
            }

            ExpiGame.get().setPlayerName(sb.toString());
            ExpiGame.get().setCharacter(PlayerCharacter.get(in.readByte()));

            in.close();
        }catch(IOException ignored) { }
    }

    private void saveData() {

        FileHandle file = Gdx.files.local(path);
        try {
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(file.write(false)));

            out.writeInt(ExpiGame.get().getPlayerName().length());
            for(char c : ExpiGame.get().getPlayerName().toCharArray()) {
                out.writeChar(c);
            }
            out.writeByte(ExpiGame.get().getCharacter().ordinal());

            out.close();
        }catch(IOException ignored) {}

    }
}
