package com.astetyne.server.backend.packets;

import java.io.Serializable;

public class PlayerLeaveActionS implements Serializable {

    private final int playerID;

    public PlayerLeaveActionS(int playerID) {
        this.playerID = playerID;
    }

    public int getPlayerID() {
        return playerID;
    }
}
