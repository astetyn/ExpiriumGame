package com.astetyne.main.net.client.actions;

import com.astetyne.main.net.netobjects.MessageAction;

public class JoinRequestActionC extends MessageAction {

    private final String name;

    public JoinRequestActionC(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
