package com.astetyne.expirium.client.gui.roots.menu;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.Res;
import com.astetyne.expirium.client.gui.widget.TextInputRoot;
import com.astetyne.expirium.client.screens.MenuScreen;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
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

        nameTF.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                menu.setRoot(new TextInputRoot(() -> menu.setRoot(ref), nameTF));
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

            out.close();
        }catch(IOException ignored) {}

    }
}
