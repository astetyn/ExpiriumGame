package com.astetyne.server.backend.packables;

import com.astetyne.main.entity.Metaable;
import com.astetyne.server.api.entities.ExpiEntity;

import java.nio.ByteBuffer;

public class PackableEntity {

    public int type;
    public int id;
    public float x, y;
    public Metaable meta;

    public PackableEntity(ExpiEntity e) {
        this.type = e.getType().getID();
        this.id = e.getID();
        this.x = e.getLocation().x;
        this.y = e.getLocation().y;
        meta = e;
    }

    public void populateWithData(ByteBuffer bb) {
        bb.putInt(type);
        bb.putInt(id);
        bb.putFloat(x);
        bb.putFloat(y);
        meta.writeMeta(bb);
    }

}
