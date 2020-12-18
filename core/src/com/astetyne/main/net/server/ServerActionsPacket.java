package com.astetyne.main.net.server;

import com.astetyne.main.net.netobjects.MessageAction;

import java.io.Serializable;
import java.util.List;

public class ServerActionsPacket implements Serializable {

    private final List<MessageAction> serverActions;

    public ServerActionsPacket(List<MessageAction> actions) {
        serverActions = actions;
    }

    public List<MessageAction> getList() {
        return serverActions;
    }

}
