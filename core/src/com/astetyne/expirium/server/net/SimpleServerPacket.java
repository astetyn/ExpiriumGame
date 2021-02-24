package com.astetyne.expirium.server.net;

public enum SimpleServerPacket {

    CLOSE_DOUBLE_INV;

    public static SimpleServerPacket get(int i) {
        return values()[i];
    }

}
