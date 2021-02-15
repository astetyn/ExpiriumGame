package com.astetyne.expirium.client.entity;

import com.astetyne.expirium.client.Res;
import com.astetyne.expirium.client.entity.animator.MoveableEntityAnimator;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.badlogic.gdx.math.Vector2;

public class SquirrelEntity extends Entity {

    public SquirrelEntity(int id, Vector2 loc, PacketInputStream in) {
        super(EntityType.SQUIRREL, id, loc);
        setAnimator(new MoveableEntityAnimator(this, Res.SQUIRREL_IDLE, Res.SQUIRREL_IDLE));
    }

}
