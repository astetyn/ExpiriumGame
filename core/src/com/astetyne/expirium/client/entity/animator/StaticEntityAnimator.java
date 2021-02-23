package com.astetyne.expirium.client.entity.animator;

import com.astetyne.expirium.client.entity.ClientEntity;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class StaticEntityAnimator extends EntityAnimator {

    private final TextureRegion texture;

    public StaticEntityAnimator(ClientEntity entity, TextureRegion texture) {
        super(entity);
        this.texture = texture;
    }

    @Override
    public void draw(SpriteBatch batch) {
        Vector2 loc = entity.getLocation();
        float angle = entity.getAngle();
        batch.draw(texture, loc.x, loc.y, 0, 0, w, h, 1, 1, (float) (angle*180/Math.PI));
    }
}
