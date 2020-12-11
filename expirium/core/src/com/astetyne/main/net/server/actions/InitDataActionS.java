package com.astetyne.main.net.server.actions;

import com.astetyne.main.net.netobjects.SVector;
import com.astetyne.main.net.netobjects.SPlayer;

import java.util.List;

public class InitDataActionS extends ServerAction {

    private final int playerID;
    private final SVector playerLocation;
    private final List<SPlayer> playersEntities;

    public InitDataActionS(int playerID, SVector playerLocation, List<SPlayer> playersEntities) {
        this.playerID = playerID;
        this.playerLocation = playerLocation;
        this.playersEntities = playersEntities;
    }

    public SVector getPlayerLocation() {
        return playerLocation;
    }

    public List<SPlayer> getPlayersEntities() {
        return playersEntities;
    }

    public int getPlayerID() {
        return playerID;
    }
}
