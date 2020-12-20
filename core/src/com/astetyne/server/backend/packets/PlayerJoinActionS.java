package com.astetyne.server.backend.packets;

import com.astetyne.main.net.netobjects.SVector;
import com.astetyne.server.api.entities.ExpiPlayer;

import java.io.Serializable;

public class PlayerJoinActionS implements Serializable {

    private final int playerID;
    private final String playerName;
    private final SVector location;

    public PlayerJoinActionS(ExpiPlayer cp) {
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
