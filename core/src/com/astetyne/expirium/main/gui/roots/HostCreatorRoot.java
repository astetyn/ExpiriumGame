package com.astetyne.expirium.main.gui.roots;

import com.astetyne.expirium.main.ExpiGame;
import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.screens.LauncherScreen;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.main.utils.Utils;
import com.astetyne.expirium.server.api.world.WorldSettings;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.net.Inet4Address;

public class HostCreatorRoot extends Table {

    private final Table savedWorlds;
    private FileHandle selectedWorld;
    private Label lastWorld;
    private final TextButton launchButton;

    public HostCreatorRoot() {

        Table leftTable = new Table();
        Table rightTable = new Table();

        // saved world table
        Label savedWorldsTitle = new Label("Saved worlds", Res.LABEL_STYLE);
        savedWorldsTitle.setAlignment(Align.center);
        launchButton = new TextButton("Launch!", Res.TEXT_BUTTON_STYLE);
        Image deleteWorldButton = new Image(Res.FOOD_ICON);

        savedWorlds = new Table();
        rebuildSavedWorldsTable();

        ScrollPane worldsPane = new ScrollPane(savedWorlds);

        launchButton.setColor(Color.GRAY);
        launchButton.setDisabled(true);
        launchButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ExpiGame.get().startServer(new WorldSettings(selectedWorld.name()), false, Consts.SERVER_DEFAULT_TPS, Consts.SERVER_PORT);
                LauncherScreen.get().setRoot(new LoadingRoot("Loading world..."));
                ExpiGame.get().startClient((Inet4Address) Inet4Address.getLoopbackAddress());
            }
        });

        deleteWorldButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(selectedWorld == null) return;
                selectedWorld.delete();
                rebuildSavedWorldsTable();
            }
        });

        leftTable.add(savedWorldsTitle).expandX().align(Align.top).padTop(50);
        leftTable.row();
        leftTable.add(worldsPane).growY().width(600);
        leftTable.row();
        leftTable.add(launchButton).width(400).height(100).align(Align.bottom).padBottom(50);
        leftTable.add(deleteWorldButton).width(100).height(Utils.percFromW(100)).align(Align.right).padLeft(30);
        leftTable.background(new TextureRegionDrawable(Res.INV_CHOOSE_BACK));

        // world creator table
        Label worldCreatorTitle = new Label("Create new world", Res.LABEL_STYLE);
        worldCreatorTitle.setAlignment(Align.center);
        Image returnButton = new Image(Res.CROSS_ICON);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                LauncherScreen.get().setRoot(new LauncherRoot());
            }
        });
        TextField tf = new TextField("", Res.TEXT_FIELD_STYLE);
        tf.setMessageText("Enter world name");
        tf.setAlignment(Align.center);
        tf.setTextFieldFilter((textField1, c) -> Character.toString(c).matches("^[0-9a-zA-Z ]"));
        TextButton createNewButton = new TextButton("Create new!", Res.TEXT_BUTTON_STYLE);
        createNewButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(tf.getText().trim().isEmpty()) {
                    //todo: check: existujuce meno + vymazat svet (vo saved worlds)
                    //todo: oznamit to?
                    return;
                }
                LauncherScreen.get().setRoot(new LoadingRoot("Creating world..."));
                ExpiGame.get().startServer(new WorldSettings(tf.getText().trim(), 60, 60, 0), true, Consts.SERVER_DEFAULT_TPS, Consts.SERVER_PORT);
                ExpiGame.get().startClient((Inet4Address) Inet4Address.getLoopbackAddress());
            }
        });

        rightTable.add(worldCreatorTitle).expandX().padTop(50).align(Align.top);
        rightTable.add(returnButton).width(100).height(Utils.percFromW(100)).align(Align.topRight).pad(10, 0, 0, 20);
        rightTable.row();
        rightTable.add(tf).width(600).height(100).expandY().colspan(2);
        rightTable.row();
        rightTable.add(createNewButton).width(400).height(100).align(Align.bottom).padBottom(50).colspan(2);
        rightTable.background(new TextureRegionDrawable(Res.INV_CHOOSE_BACK));

        add(leftTable).width(1000).growY();
        add(rightTable).width(1000).growY();
    }

    private void rebuildSavedWorldsTable() {
        savedWorlds.clear();
        for(FileHandle fh : Gdx.files.local("worlds").list()) {
            Table world = new Table();
            Label worldLabel = new Label(fh.name() +" ("+Math.round(fh.length() * 10 / 1024f)/10f+"kB)", Res.LABEL_STYLE);
            worldLabel.setAlignment(Align.center);
            worldLabel.setColor(Color.LIGHT_GRAY);
            world.add(worldLabel).grow();
            world.setBackground(Res.RECIPE_BACK);
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