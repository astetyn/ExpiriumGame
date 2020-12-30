package com.astetyne.expirium.main.stages;

import com.astetyne.expirium.main.ExpiriumGame;
import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.utils.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class LauncherStage extends ExpiStage {

    private boolean hostingServer;
    private final Table table;

    public LauncherStage() {

        hostingServer = true;

        table = new Table();

        TextField textField = new TextField("", Res.TEXT_FIELD_STYLE);
        textField.setMessageText("Enter your name");

        final TextField textField2 = new TextField("127.0.0.1", Res.TEXT_FIELD_STYLE);
        textField2.setMessageText("Enter the ip address");
        textField2.setTextFieldFilter((textField1, c) -> Character.toString(c).matches("^[0-9.]"));
        textField2.setVisible(false);

        TextButton serverButton = new TextButton("Host a server.", Res.TEXT_BUTTON_STYLE);
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

        TextButton button = new TextButton("Connect", Res.TEXT_BUTTON_STYLE);

        table.add(serverButton).width(500).height(100);
        table.row();
        table.add(textField).width(500).height(100);
        table.row();
        table.add(textField2).width(500).height(100);
        table.row();
        table.add(button).width(500).height(100);

        textField.setAlignment(Align.center);
        textField2.setAlignment(Align.center);

        table.setFillParent(true);
        if(Constants.DEBUG) table.setDebug(true);

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
        Res.ARIAL_FONT.getData().setScale((float)Gdx.graphics.getHeight() / Gdx.graphics.getWidth(), 1);
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void onServerUpdate() {

    }

    @Override
    public void onServerFail() {
        table.setVisible(true);
    }
}
