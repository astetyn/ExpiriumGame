package com.astetyne.expirium.client.entity;

import com.astetyne.expirium.client.resources.PlayerCharacter;
import com.astetyne.expirium.client.world.ClientWorld;
import com.badlogic.gdx.math.Vector2;

public class MainClientPlayer extends ClientPlayer {

    public MainClientPlayer(ClientWorld world, short id, Vector2 loc, PlayerCharacter character) {
        super(world, id, loc, character);
    }
}
