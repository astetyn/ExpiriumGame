package com.astetyne.expirium.client.entity;

import com.astetyne.expirium.client.Res;
import com.astetyne.expirium.client.entity.animator.PlayerAnimator;
import com.badlogic.gdx.math.Vector2;

public class MainPlayer extends Entity {

    public MainPlayer(int id, Vector2 loc) {
        super(EntityType.PLAYER, id, loc);
        setAnimator(new PlayerAnimator(this, Res.PLAYER_IDLE_ANIM, Res.PLAYER_MOVE_ANIM));
    }
}
