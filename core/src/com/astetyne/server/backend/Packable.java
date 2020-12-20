package com.astetyne.server.backend;

public interface Packable {

    int getPacketID();

    byte[] toByteArray();

}
