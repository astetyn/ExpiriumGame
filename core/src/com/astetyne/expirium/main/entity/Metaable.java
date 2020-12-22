package com.astetyne.expirium.main.entity;

import java.nio.ByteBuffer;

public interface Metaable extends MetaReadable {

    void writeMeta(ByteBuffer bb);

}
