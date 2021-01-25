package com.astetyne.expirium.client.gui.roots.menu;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.Res;
import com.astetyne.expirium.client.resources.GuiRes;
import com.astetyne.expirium.client.screens.MenuScreen;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.Utils;
import com.astetyne.expirium.server.ServerPreferences;
import com.astetyne.expirium.server.api.world.generator.CreateWorldPreferences;
import com.astetyne.expirium.server.api.world.generator.LoadWorldPreferences;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostCreatorRoot extends Table {

    private final Table savedWorlds;
    private FileHandle selectedWorld;
    private Label lastWorld;
    private final TextButton launchButton;

    public HostCreatorRoot(MenuScreen menu) {

        Table leftTable = new Table();
        Table rightTable = new Table();

        // saved world table
        Label savedWorldsTitle = new Label("Saved worlds", Res.LABEL_STYLE);
        savedWorldsTitle.setAlignment(Align.center);
        launchButton = new TextButton("Launch!", Res.TEXT_BUTTON_STYLE);
        Image deleteWorldButton = new Image(GuiRes.TRASH_ICON.getDrawable());

        savedWorlds = new Table();
        rebuildSavedWorldsTable();

        ScrollPane worldsPane = new ScrollPane(savedWorlds);

        launchButton.setColor(Color.GRAY);
        launchButton.setDisabled(true);
        launchButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                LoadWorldPreferences worldPref = new LoadWorldPreferences(selectedWorld.name());
                ServerPreferences pref = new ServerPreferences(worldPref, Consts.SERVER_DEFAULT_TPS, Consts.SERVER_PORT);
                ExpiGame.get().startServer(pref, menu);
                try {
                    ExpiGame.get().startClient(InetAddress.getLocalHost());
                }catch(UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        });

        deleteWorldButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(selectedWorld == null) return;
                Utils.deleteDir(selectedWorld);
                rebuildSavedWorldsTable();
            }
        });

        leftTable.add(savedWorldsTitle).expandX().align(Align.top).padTop(50);
        leftTable.row();
        leftTable.add(worldsPane).growY().width(600);
        leftTable.row();
        leftTable.add(launchButton).width(400).height(100).align(Align.bottom).padBottom(50);
        leftTable.add(deleteWorldButton).width(100).height(Utils.percFromW(100)).align(Align.right).padLeft(30);
        leftTable.background(GuiRes.FRAME_SQUARE.getDrawable());

        // world creator table
        Label worldCreatorTitle = new Label("Create new world", Res.LABEL_STYLE);
        worldCreatorTitle.setAlignment(Align.center);
        Image returnButton = new Image(Res.CROSS_ICON);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                menu.setRoot(new MainMenuRoot("", menu));
            }
        });
        TextField tf = new TextField("", Res.TEXT_FIELD_STYLE);
        tf.setMessageText("Enter world name");
        tf.setAlignment(Align.center);
        tf.setTextFieldFilter((textField1, c) -> Character.toString(c).matches("^[0-9a-zA-Z ]"));
        if(Consts.DEBUG) tf.setText("test"+Gdx.files.local("worlds").list().length);
        TextButton createNewButton = new TextButton("Create new!", Res.TEXT_BUTTON_STYLE);
        createNewButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String worldName = tf.getText().trim();
                if(worldName.isEmpty()) {
                    menu.addWarning("Add a name please.", 1000, Color.ORANGE);
                    return;
                }
                for(FileHandle fh : Gdx.files.local("worlds").list()) {
                    if(fh.name().equals(worldName)) {
                        menu.addWarning("This world name already exists.", 1000, Color.RED);
                        return;
                    }
                }
                menu.setRoot(new LoadingRoot("Creating world..."));
                CreateWorldPreferences worldPref = new CreateWorldPreferences(worldName, 1000, 256, 0);
                ServerPreferences pref = new ServerPreferences(worldPref, Consts.SERVER_DEFAULT_TPS, Consts.SERVER_PORT);
                ExpiGame.get().startServer(pref, menu);
                try {
                    ExpiGame.get().startClient(Inet4Address.getLocalHost());
                }catch(UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        });

        rightTable.add(worldCreatorTitle).expandX().padTop(50).align(Align.top);
        rightTable.add(returnButton).width(100).height(Utils.percFromW(100)).align(Align.topRight).pad(10, 0, 0, 20);
        rightTable.row();
        rightTable.add(tf).width(600).height(100).expandY().colspan(2);
        rightTable.row();
        rightTable.add(createNewButton).width(400).height(100).align(Align.bottom).padBottom(50).colspan(2);
        rightTable.background(GuiRes.FRAME_SQUARE.getDrawable());

        add(leftTable).width(1000).growY();
        add(rightTable).width(1000).growY();
    }

    private void rebuildSavedWorldsTable() {
        savedWorlds.clear();
        for(FileHandle fh : Gdx.files.local("worlds").list()) {
            int size = Utils.getDirSize(fh);
            Table world = new Table();
            Label worldLabel = new Label(fh.name() +" ("+Math.round(size * 10 / 1024f)/10f+"kB)", Res.LABEL_STYLE);
            worldLabel.setAlignment(Align.center);
            worldLabel.setColor(Color.LIGHT_GRAY);
            world.add(worldLabel).grow();
            world.setBackground(GuiRes.FRAME_YELLOW.getDrawable());
            world.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(lastWorld == worldLabel) return;
                    selectedWorld = fh;
                    launchButton.setColor(Color.LIGHT_GRAY);
                    launchButton.setDisabled(false);
                    worldLabel.setColor(Color.WHITE);
                    if(lastWorld != null) lastWorld.setColor(Color.LIGHT_GRAY);
                    lastWorld = worldLabel;
                }
            });
            savedWorlds.add(world).growX().height(100);
            savedWorlds.row();
        }
    }
}
