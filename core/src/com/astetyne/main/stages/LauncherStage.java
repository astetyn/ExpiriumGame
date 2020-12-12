package com.astetyne.main.stages;

import com.astetyne.main.ExpiriumGame;
import com.astetyne.main.TextureManager;
import com.astetyne.main.net.server.actions.InitDataActionS;
import com.astetyne.main.net.server.actions.ServerAction;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.List;

public class LauncherStage extends ExpiStage {

    public LauncherStage() {

        TextField textField = new TextField("", TextureManager.DEFAULT_SKIN);
        textField.setMessageText("Enter your name");

        final TextField textField2 = new TextField("127.0.0.1", TextureManager.DEFAULT_SKIN);
        textField2.setMessageText("Enter the ip address");
        textField2.setTextFieldFilter(new TextField.TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField textField1, char c) {
                return Character.toString(c).matches("^[0-9.]");
            }
        });
        textField2.setVisible(false);

        final CheckBox checkBox = new CheckBox("Host a server.", TextureManager.DEFAULT_SKIN);
        checkBox.setChecked(true);

        TextButton button = new TextButton("Connect", TextureManager.DEFAULT_SKIN);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(checkBox.isChecked()) {
                    ExpiriumGame.getGame().startServer();
                    ExpiriumGame.getGame().startClient("127.0.0.1");
                }else {
                    ExpiriumGame.getGame().startClient(textField2.getText());
                }
            }
        });

        Table table = new Table();
        table.add(checkBox);
        table.row();
        table.add(textField);
        table.add(textField2);
        table.row();
        table.add(button);

        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                textField2.setVisible(!checkBox.isChecked());
            }
        });

        table.setFillParent(true);

        stage.addActor(table);

        //todo: pridat nejake GUI elementy pre pripojenie na server a zadanie mena

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
    public void onServerUpdate(List<ServerAction> actions) {
        for(ServerAction serverAction : actions) {
            if(serverAction instanceof InitDataActionS) {
                ExpiriumGame.getGame().setCurrentStage(new RunningGameStage());
                ExpiriumGame.getGame().getCurrentStage().onServerUpdate(actions);
                return;
            }
        }
    }
}
