package com.astetyne.expirium.client.entity;

import com.astetyne.expirium.client.resources.PlayerCharacter;
import com.astetyne.expirium.client.resources.Res;
import com.astetyne.expirium.client.utils.Utils;
import com.astetyne.expirium.client.world.ClientWorld;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class FriendClientPlayer extends ClientPlayer {

    private final String name;
    private final float nameOffset;

    public FriendClientPlayer(ClientWorld world, short id, Vector2 loc, PacketInputStream in) {
        super(world, id, loc, PlayerCharacter.get(in.getByte()));
        name = in.getString();
        nameOffset = Utils.getTextWidth(name, Res.WORLD_FONT) / 2;
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
        Res.WORLD_FONT.draw(batch, name, getCenter().x - nameOffset, getLocation().y + 2f); // 2 comes from observing
    }
}
