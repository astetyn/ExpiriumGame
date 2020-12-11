package com.astetyne.main.net.server;

import com.astetyne.main.net.server.actions.ServerAction;

import java.io.Serializable;
import java.util.List;

public class ServerActionsPacket implements Serializable {

    private final List<ServerAction> serverActions;

    public ServerActionsPacket(List<ServerAction> actions) {
        serverActions = actions;
    }

    public List<ServerAction> getList() {
        return serverActions;
    }

}
