package com.astetyne.main.net.server.actions;

public class PlayerLeaveActionS extends ServerAction {

    private final int playerID;

    public PlayerLeaveActionS(int playerID) {
        this.playerID = playerID;
    }

    public int getPlayerID() {
        return playerID;
    }
}
