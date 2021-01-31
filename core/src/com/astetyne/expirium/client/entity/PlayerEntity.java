package com.astetyne.expirium.client.entity;

import com.astetyne.expirium.client.Res;
import com.astetyne.expirium.client.entity.animator.MoveableEntityAnimator;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.utils.Utils;
import com.astetyne.expirium.server.net.PacketInputStream;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class PlayerEntity extends Entity {

    private final String name;
    private final float nameOffset;
    private long lastInteractAnim;
    private Item holdingItem;

    public PlayerEntity(int id, Vector2 loc, PacketInputStream in) {
        super(EntityType.PLAYER, id, loc);
        name = in.getString();
        setAnimator(new MoveableEntityAnimator(this, Res.PLAYER_IDLE_ANIM, Res.PLAYER_MOVE_ANIM));
        nameOffset = Utils.getTextWidth(name, Res.WORLD_FONT) / 2;
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
        Res.WORLD_FONT.draw(batch, name, getCenter().x - nameOffset, getLocation().y + 1.7f); // 1.7 comes from observing
    }
}
