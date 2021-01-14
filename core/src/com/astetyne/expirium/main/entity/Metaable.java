package com.astetyne.expirium.main.entity;

import com.astetyne.expirium.server.backend.PacketOutputStream;

public interface Metaable {

    void writeMeta(PacketOutputStream out);

}
