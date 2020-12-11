package com.astetyne.main.net.client;

import com.astetyne.main.net.client.actions.ClientAction;

import java.io.Serializable;
import java.util.List;

public class ClientActionsPacket implements Serializable {

    private final List<ClientAction> clientActions;

    public ClientActionsPacket(List<ClientAction> actions) {
        clientActions = actions;
    }

    public List<ClientAction> getList() {
        return clientActions;
    }


}
