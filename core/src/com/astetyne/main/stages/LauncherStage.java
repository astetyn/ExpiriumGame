package com.astetyne.main.stages;

import com.astetyne.main.ExpiriumGame;
import com.astetyne.main.Resources;
import com.astetyne.main.net.netobjects.MessageAction;
import com.astetyne.main.net.server.actions.InitDataActionS;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import java.util.List;

public class LauncherStage extends ExpiStage {

    private boolean hostingServer;
    private final Table table;

    public LauncherStage() {

        hostingServer = true;

        table = new Table();

        TextField textField = new TextField("", Resources.TEXT_FIELD_STYLE);
        textField.setMessageText("Enter your name");

        final TextField textField2 = new TextField("127.0.0.1", Resources.TEXT_FIELD_STYLE);
        textField2.setMessageText("Enter the ip address");
        textField2.setTextFieldFilter((textField1, c) -> Character.toString(c).matches("^[0-9.]"));
        textField2.setVisible(false);

        TextButton serverButton = new TextButton("Host a server.", Resources.TEXT_BUTTON_STYLE);
        serverButton.setColor(Color.GREEN);
        serverButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hostingServer = !hostingServer;
                textField2.setVisible(!hostingServer);
                Color c = hostingServer ? Color.GREEN : Color.LIGHT_GRAY;
                serverButton.setColor(c);
            }
        });

        TextButton button = new TextButton("Connect", Resources.TEXT_BUTTON_STYLE);

        table.add(serverButton).width(500*Gdx.graphics.getDensity()).height(100*Gdx.graphics.getDensity());
        table.row();
        table.add(textField).width(500*Gdx.graphics.getDensity()).height(100*Gdx.graphics.getDensity());
        table.row();
        table.add(textField2).width(500*Gdx.graphics.getDensity()).height(100*Gdx.graphics.getDensity());
        table.row();
        table.add(button).width(500*Gdx.graphics.getDensity()).height(100*Gdx.graphics.getDensity());

        serverButton.getLabel().setFontScale(0.5f);
        button.getLabel().setFontScale(0.5f);
        textField.getStyle().font.getData().setScale(0.9f);
        textField.setAlignment(Align.center);

        table.setFillParent(true);
        table.setDebug(true);

        stage.addActor(table);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(hostingServer) {
                    ExpiriumGame.get().startServer();
                    ExpiriumGame.get().startClient("127.0.0.1", textField.getText());
                }else {
                    ExpiriumGame.get().startClient(textField2.getText(), textField.getText());
                }
                table.setVisible(false);
            }
        });

    }

    @Override
    public void update() {
        stage.act();
    }

    @Override
    public void render() {

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
    }

    @Override
    public void resize() {
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void onServerUpdate(List<MessageAction> actions) {

        for(MessageAction serverAction : actions) {
            if(serverAction instanceof InitDataActionS) {
                ExpiriumGame.get().setCurrentStage(new GameStage());
                ExpiriumGame.get().getCurrentStage().onServerUpdate(actions);
                return;
            }
        }
    }

    @Override
    public void onServerFail() {
        table.setVisible(true);
    }
}
