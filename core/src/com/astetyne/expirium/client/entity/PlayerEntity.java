package com.astetyne.expirium.client.entity;

import com.astetyne.expirium.client.Res;
import com.astetyne.expirium.client.utils.Utils;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class PlayerEntity extends Entity {

    private final PlayerEntityAnimator animator;
    private String name;
    private final float nameOffset;

    public PlayerEntity(int id, Vector2 loc, PacketInputStream in) {
        super(EntityType.PLAYER, id, loc, 0.9f, 1.25f);
        animator = new PlayerEntityAnimator(this);
        readMeta(in);
        nameOffset = Utils.getTextWidth(name, Res.WORLD_FONT) / 2;
    }

    @Override
    public void draw(SpriteBatch batch) {
        animator.draw(batch);
        Res.WORLD_FONT.draw(batch, name, getCenter().x - nameOffset, getLocation().y + 1.7f);
    }

    @Override
    public void readMeta(PacketInputStream in) {
        name = in.getString();
    }
}
