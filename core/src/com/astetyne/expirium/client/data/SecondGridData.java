package com.astetyne.expirium.client.data;

import com.astetyne.expirium.server.net.PacketInputStream;

import java.util.ArrayList;
import java.util.List;

public class SecondGridData extends GridData {

    public final List<InvVariable> secondInvVariables;

    public SecondGridData() {
        this.secondInvVariables = new ArrayList<>();
    }

    @Override
    public void feed(PacketInputStream in) {
        super.feed(in);
        for(InvVariable secondInvVariable : secondInvVariables) {
            secondInvVariable.text = in.getShortString();
        }
    }
}
