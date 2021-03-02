package com.astetyne.expirium.client.gui.roots.menu;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.resources.Res;
import com.astetyne.expirium.client.screens.MenuScreen;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.Utils;
import com.astetyne.expirium.server.core.world.file.WorldFileManager;
import com.astetyne.expirium.server.core.world.file.WorldQuickInfo;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class WorldManagerRoot extends WidgetGroup implements MenuRootable {

    public WorldManagerRoot(MenuScreen menu) {

        Image imgLeft = new Image(Res.FRAME_SQUARE_GRAY);
        Image imgRight = new Image(Res.FRAME_SQUARE_GRAY);

        // load world side
        TextButton loadWorldButton = new TextButton("Load World", Res.TEXT_BUTTON_STYLE);
        loadWorldButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ExpiGame.get().startServer(menu, false);
                try {
                    ExpiGame.get().connectToServer(InetAddress.getLocalHost());
                }catch(UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        });

        Table savedWorldTable = new Table();
        savedWorldTable.setBackground(Res.FRAME_ROUND_GRAY);

        Label savedWorldStatusLabel = new Label("", Res.LABEL_STYLE);
        savedWorldTable.add(savedWorldStatusLabel);
        savedWorldTable.row();

        WorldQuickInfo wqi = WorldFileManager.getQuickInfo();

        if(wqi != null) {

            savedWorldStatusLabel.setText("Saved world");
            Label dayLabel = new Label("Day: "+(wqi.tick / Consts.TICKS_IN_DAY), Res.LABEL_STYLE);
            Label firstLife = new Label("First life: "+(wqi.firstLife ? "Yes" : "No"), Res.LABEL_STYLE);
            Label versionLabel = new Label("Version: "+wqi.worldVersion, Res.LABEL_STYLE);
            dayLabel.setColor(Color.ORANGE);
            firstLife.setColor(Color.ORANGE);
            versionLabel.setColor(Color.ORANGE);
            savedWorldTable.add(dayLabel).padTop(100);
            savedWorldTable.row();
            savedWorldTable.add(firstLife);
            savedWorldTable.row();
            savedWorldTable.add(versionLabel);
        }else {
            savedWorldStatusLabel.setText("No saved world");
            loadWorldButton.setDisabled(true);
            loadWorldButton.setColor(Color.GRAY);
        }

        imgLeft.setBounds(0, 0, 1000, 1000);
        addActor(imgLeft);
        savedWorldTable.setBounds(150, 200, 700, 700);
        addActor(savedWorldTable);
        loadWorldButton.setBounds(300, 50, 400, 100);
        addActor(loadWorldButton);

        // create world side
        Image returnButton = new Image(Res.CROSS_ICON);
        Label warningLabel = new Label("Note: this will delete your saved world", Res.LABEL_STYLE);
        TextButton createWorldButton = new TextButton("Create new world", Res.TEXT_BUTTON_STYLE);
        Label areYouSureLabel = new Label("Are you sure?", Res.LABEL_STYLE);
        TextButton yesButton = new TextButton("Yes", Res.TEXT_BUTTON_STYLE);

        warningLabel.setAlignment(Align.center);
        warningLabel.setColor(Color.RED);
        areYouSureLabel.setVisible(false);
        areYouSureLabel.setAlignment(Align.center);
        areYouSureLabel.setColor(Color.GRAY);
        yesButton.setVisible(false);
        yesButton.setColor(Color.GREEN);

        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                menu.setRoot(new MainMenuRoot("", menu));
            }
        });

        createWorldButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                areYouSureLabel.setVisible(true);
                yesButton.setVisible(true);
            }
        });

        yesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ExpiGame.get().startServer(menu, true);
                try {
                    ExpiGame.get().connectToServer(InetAddress.getLocalHost());
                }catch(UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        });

        imgRight.setBounds(1000, 0, 1000, 1000);
        addActor(imgRight);
        returnButton.setBounds(1880, 890, 100, Utils.percFromW(100));
        addActor(returnButton);
        warningLabel.setBounds(1100, 150, 800, 100);
        addActor(warningLabel);
        createWorldButton.setBounds(1300, 50, 400, 100);
        addActor(createWorldButton);
        areYouSureLabel.setBounds(1100, 700, 800, 100);
        addActor(areYouSureLabel);
        yesButton.setBounds(1300, 600, 400,  100);
        addActor(yesButton);

    }

    @Override
    public Actor getActor() {
        return this;
    }

    @Override
    public void onEnd() {

    }
}
