package com.astetyne.expirium.main.entity;

import com.badlogic.gdx.math.Vector2;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.HashMap;

public enum EntityType {

    PLAYER(0, PlayerEntity.class),
    DROPPED_ITEM(1, DroppedItemEntity.class);

    private static final HashMap<Integer, EntityType> map;
    static {
        map = new HashMap<>();
        for(EntityType et : EntityType.values()) {
            map.put(et.id, et);
        }
    }
    public static EntityType getType(int id) {
        return map.get(id);
    }

    int id;
    Class<? extends Entity> entityClazz;

    EntityType(int id, Class<? extends Entity> entityClazz) {
        this.id = id;
        this.entityClazz = entityClazz;
    }

    public int getID() {
        return id;
    }

    public Entity initEntity(ByteBuffer bb) {
        try {
            int id = bb.getInt();
            Vector2 loc = new Vector2(bb.getFloat(), bb.getFloat());
            return entityClazz.getConstructor(int.class, Vector2.class, ByteBuffer.class).newInstance(id, loc, bb);
        }catch(NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
