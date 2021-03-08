package com.astetyne.expirium.client.entity;

import com.astetyne.expirium.client.world.ClientWorld;
import com.astetyne.expirium.server.core.entity.DroppedItem;
import com.astetyne.expirium.server.core.entity.Entity;
import com.astetyne.expirium.server.core.entity.Frog;
import com.astetyne.expirium.server.core.entity.Squirrel;
import com.astetyne.expirium.server.core.entity.player.Player;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.badlogic.gdx.math.Vector2;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public enum EntityType {

    PLAYER(FriendClientPlayer.class, Player.class, 0.9f, 1.5f),
    DROPPED_ITEM(DroppedItemClientEntity.class, DroppedItem.class, 0.4f, 0.4f),
    SQUIRREL(SquirrelClientEntity.class, Squirrel.class, 0.6f, 0.6f),
    FROG(FrogClientEntity.class, Frog.class, 0.6f, 0.49f),
    ;

    Class<? extends ClientEntity> entityClazz;
    Class<? extends Entity> entityClazz2;
    float width, height;

    EntityType(Class<? extends ClientEntity> entityClazz, Class<? extends Entity> entityClazz2, float width, float height) {
        this.entityClazz = entityClazz;
        this.entityClazz2 = entityClazz2;
        this.width = width;
        this.height = height;
    }

    public int getID() {
        return id;
    }

    public ClientEntity initEntity(ClientWorld world, PacketInputStream in) {
        try {
            short id = in.getShort();
            Vector2 loc = new Vector2(in.getFloat(), in.getFloat());
            return entityClazz.getConstructor(ClientWorld.class, short.class, Vector2.class, PacketInputStream.class).newInstance(world, id, loc, in);
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

    public Class<? extends Entity> getEntityClass() {
        return entityClazz2;
    }

}
