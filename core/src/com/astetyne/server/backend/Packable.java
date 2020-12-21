package com.astetyne.server.backend;

import java.nio.ByteBuffer;

public interface Packable {

    int getPacketID();

    void populateWithData(ByteBuffer bb);

}
