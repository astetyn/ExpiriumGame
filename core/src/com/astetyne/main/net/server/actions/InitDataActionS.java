package com.astetyne.main.net.server.actions;

import com.astetyne.main.net.netobjects.MessageAction;
import com.astetyne.main.net.netobjects.SEntity;
import com.astetyne.main.net.netobjects.SVector;

import java.util.List;

public class InitDataActionS extends MessageAction {

    private final int playerID;
    private final SVector playerLocation;
    private final List<SEntity> entities;
    private final int numberOfChunks;

    public InitDataActionS(int playerID, SVector playerLocation, List<SEntity> entities, int numberOfChunks) {
        this.playerID = playerID;
        this.playerLocation = playerLocation;
        this.entities = entities;
        this.numberOfChunks = numberOfChunks;
    }

    public SVector getPlayerLocation() {
        return playerLocation;
    }

    public List<SEntity> getEntities() {
        return entities;
    }

    public int getPlayerID() {
        return playerID;
    }

    public int getNumberOfChunks() {
        return numberOfChunks;
    }
}
