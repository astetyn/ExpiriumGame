package com.astetyne.expirium.client.entity;

import com.astetyne.expirium.client.entity.animator.PlayerAnimator;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.resources.PlayerCharacter;
import com.astetyne.expirium.client.world.ClientWorld;
import com.badlogic.gdx.math.Vector2;

public abstract class ClientPlayer extends ClientEntity {

    private Item itemInHand;

    public ClientPlayer(ClientWorld world, short id, Vector2 loc, PlayerCharacter character) {
        super(world, EntityType.PLAYER, id, loc);
        setAnimator(new PlayerAnimator(this, character));
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
