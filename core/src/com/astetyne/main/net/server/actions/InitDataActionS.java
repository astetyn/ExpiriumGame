package com.astetyne.main.net.server.actions;

import com.astetyne.main.net.netobjects.SPlayer;
import com.astetyne.main.net.netobjects.SVector;

import java.util.List;

public class InitDataActionS extends ServerAction {

    private final int playerID;
    private final SVector playerLocation;
    private final List<SPlayer> playersEntities;
    private final int numberOfChunks;

    public InitDataActionS(int playerID, SVector playerLocation, List<SPlayer> playersEntities, int numberOfChunks) {
        this.playerID = playerID;
        this.playerLocation = playerLocation;
        this.playersEntities = playersEntities;
        this.numberOfChunks = numberOfChunks;
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

    public int getNumberOfChunks() {
        return numberOfChunks;
    }
}
