package com.astetyne.expirium.client.gui.roots.menu;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.net.BroadcastListener;
import com.astetyne.expirium.client.resources.Res;
import com.astetyne.expirium.client.screens.MenuScreen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class ServerListRoot extends WidgetGroup implements MenuRootable {

    private final Table availableServersTable;
    private final TextButton joinButton;

    private BroadcastListener.AvailableServer chosenServer;
    private Table lastChosenTable;

    public ServerListRoot(MenuScreen menu) {

        Label infoLabel = new Label("Searching for LAN servers...", Res.LABEL_STYLE);
        infoLabel.setAlignment(Align.center);

        TextButton cancelButton = new TextButton("Cancel", Res.TEXT_BUTTON_STYLE);
        joinButton = new TextButton("Join", Res.TEXT_BUTTON_STYLE);

        joinButton.setDisabled(true);
        joinButton.setColor(Color.GRAY);

        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menu.setRoot(new MainMenuRoot("", menu));
            }
        });

        joinButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menu.setRoot(new LoadingRoot("Connecting to server..."));
                ExpiGame.get().connectToServer(chosenServer.address);
            }
        });

        availableServersTable = new Table();

        ScrollPane availableServersScroll = new ScrollPane(availableServersTable);

        Image img = new Image(Res.FRAME_ROUND_GRAY);

        int left = 300;
        int width = 1400;
        int bottom = 100;

        img.setBounds(left, bottom, width, 800);
        addActor(img);

        infoLabel.setBounds(left, 820, width, 60);
        addActor(infoLabel);

        availableServersScroll.setBounds(left + 20, 300, width - 40, 400);
        addActor(availableServersScroll);

        cancelButton.setBounds(left + 10, bottom + 10, width/2f - 20, 100);
        addActor(cancelButton);
        joinButton.setBounds(left + width / 2f + 10, bottom + 10, width/2f - 20, 100);
        addActor(joinButton);

        populateTableWithServers();
    }

    public void act(float delta) {

        if(ExpiGame.get().getMulticastListener().hasChanged()) populateTableWithServers();

        super.act(delta);
    }

    private void populateTableWithServers() {

        BroadcastListener listener = ExpiGame.get().getMulticastListener();

        availableServersTable.clear();
        for(BroadcastListener.AvailableServer server : listener.getAvailableServers()) {
            Label name = new Label(server.owner+"'s", Res.LABEL_STYLE);
            Label version = new Label("ver. "+server.version, Res.LABEL_STYLE);
            Table t = new Table();
            t.add(name).width(800).height(100).padLeft(50);
            t.add(version).width(200).height(100).align(Align.center);
            t.setBackground(Res.FRAME_ROUND_GRAY_TRANSP);

            t.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(lastChosenTable != null) lastChosenTable.setBackground(Res.FRAME_ROUND_GRAY);
                    chosenServer = server;
                    lastChosenTable = t;
                    t.setBackground(Res.FRAME_ROUND_YELLOW);
                    joinButton.setColor(Color.YELLOW);
                    joinButton.setDisabled(false);
                }
            });

            availableServersTable.add(t).width(1000).height(100);
            availableServersTable.row();
        }

    }

    @Override
    public Actor getActor() {
        return this;
    }

    @Override
    public void onEnd() {

    }
}
