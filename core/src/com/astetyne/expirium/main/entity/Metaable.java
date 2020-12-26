package com.astetyne.expirium.main.entity;

import com.astetyne.expirium.server.backend.PacketOutputStream;

public interface Metaable extends MetaReadable {

    void writeMeta(PacketOutputStream out);

}
