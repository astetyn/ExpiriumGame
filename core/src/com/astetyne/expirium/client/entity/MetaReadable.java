package com.astetyne.expirium.client.entity;

import com.astetyne.expirium.server.net.PacketInputStream;

public interface MetaReadable {

    void readMeta(PacketInputStream in);

}
