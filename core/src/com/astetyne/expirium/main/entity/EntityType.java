package com.astetyne.expirium.main.entity;

import com.astetyne.expirium.server.api.entity.ExpiDroppedItem;
import com.astetyne.expirium.server.api.entity.ExpiEntity;
import com.astetyne.expirium.server.api.entity.ExpiPlayer;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.badlogic.gdx.math.Vector2;

import java.io.DataInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public enum EntityType {

    PLAYER(PlayerEntity.class, ExpiPlayer.class),
    DROPPED_ITEM(DroppedItemEntity.class, ExpiDroppedItem.class);

    Class<? extends Entity> entityClazz;
    Class<? extends ExpiEntity> entityClazz2;

    EntityType(Class<? extends Entity> entityClazz, Class<? extends ExpiEntity> entityClazz2) {
        this.entityClazz = entityClazz;
        this.entityClazz2 = entityClazz2;
    }

    public int getID() {
        return id;
    }

    public Entity initEntity(PacketInputStream in) {
        try {
            int id = in.getInt();
            Vector2 loc = new Vector2(in.getFloat(), in.getFloat());
            return entityClazz.getConstructor(int.class, Vector2.class, PacketInputStream.class).newInstance(id, loc, in);
        }catch(NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ExpiEntity initEntity(DataInputStream in) {
        try {
            return entityClazz2.getConstructor(DataInputStream.class).newInstance(in);
        }catch(NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    int id;
    private static final HashMap<Integer, EntityType> map;
    static {
        map = new HashMap<>();
        int i = 0;
        for(EntityType et : EntityType.values()) {
            et.id = i;
            map.put(i, et);
            i++;
        }
    }
    public static EntityType getType(int id) {
        return map.get(id);
    }

}
