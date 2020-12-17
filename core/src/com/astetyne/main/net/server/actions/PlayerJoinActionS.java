package com.astetyne.main.net.server.actions;

import com.astetyne.main.net.netobjects.SVector;
import com.astetyne.main.net.server.entities.ServerPlayer;

public class PlayerJoinActionS extends ServerAction {

    private final int playerID;
    private final String playerName;
    private final SVector location;

    public PlayerJoinActionS(ServerPlayer cp) {
        this.playerID = cp.getID();
        this.playerName = cp.getName();
        this.location = new SVector(cp.getLocation());
    }

    public int getPlayerID() {
        return playerID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public SVector getLocation() {
        return location;
    }
}
