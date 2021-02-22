package com.astetyne.expirium.client.entity;

import com.astetyne.expirium.server.core.entity.ExpiDroppedItem;
import com.astetyne.expirium.server.core.entity.ExpiEntity;
import com.astetyne.expirium.server.core.entity.Squirrel;
import com.astetyne.expirium.server.core.entity.player.ExpiPlayer;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.badlogic.gdx.math.Vector2;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public enum EntityType {

    PLAYER(FriendPlayer.class, ExpiPlayer.class, 0.9f, 1.5f),
    DROPPED_ITEM(DroppedItemEntity.class, ExpiDroppedItem.class, 0.5f, 0.5f),
    SQUIRREL(SquirrelEntity.class, Squirrel.class, 0.6f, 0.6f),
    ;

    Class<? extends Entity> entityClazz;
    Class<? extends ExpiEntity> entityClazz2;
    float width, height;

    EntityType(Class<? extends Entity> entityClazz, Class<? extends ExpiEntity> entityClazz2, float width, float height) {
        this.entityClazz = entityClazz;
        this.entityClazz2 = entityClazz2;
        this.width = width;
        this.height = height;
    }

    public int getID() {
        return id;
    }

    public Entity initEntity(PacketInputStream in) {
        try {
            short id = in.getShort();
            Vector2 loc = new Vector2(in.getFloat(), in.getFloat());
            return entityClazz.getConstructor(short.class, Vector2.class, PacketInputStream.class).newInstance(id, loc, in);
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

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Class<? extends ExpiEntity> getEntityClass() {
        return entityClazz2;
    }

}
