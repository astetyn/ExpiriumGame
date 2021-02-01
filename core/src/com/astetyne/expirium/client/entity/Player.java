package com.astetyne.expirium.client.entity;

import com.astetyne.expirium.client.Res;
import com.astetyne.expirium.client.entity.animator.PlayerAnimator;
import com.astetyne.expirium.client.items.Item;
import com.badlogic.gdx.math.Vector2;

public abstract class Player extends Entity {

    private Item itemInHand;

    public Player(int id, Vector2 loc) {
        super(EntityType.PLAYER, id, loc);
        setAnimator(new PlayerAnimator(this, Res.PLAYER_IDLE_ANIM, Res.PLAYER_MOVE_ANIM));
        itemInHand = Item.EMPTY;
    }

    public void onHandPunch() {
        ((PlayerAnimator)getAnimator()).handInteract();
    }

    public Item getItemInHand() {
        return itemInHand;
    }

    public void setItemInHand(Item itemInHand) {
        this.itemInHand = itemInHand;
    }
}
