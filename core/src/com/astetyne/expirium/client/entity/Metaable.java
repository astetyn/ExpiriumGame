package com.astetyne.expirium.client.entity;

import com.astetyne.expirium.server.net.PacketOutputStream;

public interface Metaable {

    void writeInitClientMeta(PacketOutputStream out);

}
