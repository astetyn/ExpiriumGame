package com.astetyne.main.net.server.actions;

import com.astetyne.main.net.netobjects.MessageAction;

public class PlayerLeaveActionS extends MessageAction {

    private final int playerID;

    public PlayerLeaveActionS(int playerID) {
        this.playerID = playerID;
    }

    public int getPlayerID() {
        return playerID;
    }
}
