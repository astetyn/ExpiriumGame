package com.astetyne.main.stages;

import com.astetyne.main.ExpiriumGame;
import com.astetyne.main.net.server.actions.InitDataActionS;
import com.astetyne.main.net.server.actions.ServerAction;

import java.util.List;

public class LauncherStage extends Stage {

    public LauncherStage() {

        //todo: pridat nejake GUI elementy pre pripojenie na server a zadanie mena

    }

    @Override
    public void update() {

    }

    @Override
    public void render() {

    }

    @Override
    public void resize() {

    }

    @Override
    public void dispose() {

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
