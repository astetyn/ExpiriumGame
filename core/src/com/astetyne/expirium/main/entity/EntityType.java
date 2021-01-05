package com.astetyne.expirium.main.entity;

import com.astetyne.expirium.main.ExpiGame;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.badlogic.gdx.math.Vector2;

import java.lang.reflect.InvocationTargetException;
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

    public Entity initEntity() {
        PacketInputStream in = ExpiGame.get().getClientGateway().getIn();
        try {
            int id = in.getInt();
            Vector2 loc = new Vector2(in.getFloat(), in.getFloat());
            return entityClazz.getConstructor(int.class, Vector2.class, PacketInputStream.class).newInstance(id, loc, in);
        }catch(NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
