package com.astetyne.expirium.server.core;

import com.astetyne.expirium.server.core.world.file.WorldBuffer;

public interface WorldSaveable {

    void writeData(WorldBuffer out);

}
