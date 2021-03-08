package com.astetyne.expirium.client.entity;

import com.astetyne.expirium.client.entity.animator.MoveableEntityAnimator;
import com.astetyne.expirium.client.resources.Res;
import com.astetyne.expirium.client.world.ClientWorld;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.badlogic.gdx.math.Vector2;

public class FrogClientEntity extends ClientEntity {

    public FrogClientEntity(ClientWorld world, short id, Vector2 loc, PacketInputStream in) {
        super(world, EntityType.FROG, id, loc);
        setAnimator(new MoveableEntityAnimator(this, Res.FROG_IDLE, Res.FROG_IDLE));
    }

}
