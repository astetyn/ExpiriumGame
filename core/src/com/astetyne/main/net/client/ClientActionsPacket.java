package com.astetyne.main.net.client;

import com.astetyne.main.net.netobjects.MessageAction;

import java.io.Serializable;
import java.util.List;

public class ClientActionsPacket implements Serializable {

    private final List<MessageAction> clientActions;

    public ClientActionsPacket(List<MessageAction> actions) {
        clientActions = actions;
    }

    public List<MessageAction> getList() {
        return clientActions;
    }


}
