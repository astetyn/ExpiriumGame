package com.astetyne.main.net.client.actions;

public class JoinRequestActionC extends ClientAction {

    private final String name;

    public JoinRequestActionC(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
