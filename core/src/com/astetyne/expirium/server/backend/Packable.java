package com.astetyne.expirium.server.backend;

import java.nio.ByteBuffer;

public interface Packable {

    int getPacketID();

    void populateWithData(ByteBuffer bb);

}
