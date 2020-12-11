package com.astetyne.main.stages;

import com.astetyne.main.ExpiriumGame;
import com.astetyne.main.gui.elements.EditableTextElement;
import com.astetyne.main.net.server.actions.InitDataActionS;
import com.astetyne.main.net.server.actions.ServerAction;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.List;

public class LauncherStage extends Stage {

    public LauncherStage() {

        EditableTextElement editableTextElement = new EditableTextElement(50, 50, 200, "sem pis", 0.5f);

        ExpiriumGame.getGame().getGui().addElement(editableTextElement);

        //todo: pridat nejake GUI elementy pre pripojenie na server a zadanie mena

    }

    @Override
    public void update() {

    }

    @Override
    public void render() {

        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        ExpiriumGame.getGame().getGui().render(batch);
        batch.end();
    }

    @Override
    public void resize() {

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
