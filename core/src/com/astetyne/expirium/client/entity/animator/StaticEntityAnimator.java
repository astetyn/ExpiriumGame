package com.astetyne.expirium.client.entity.animator;

import com.astetyne.expirium.client.entity.Entity;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class StaticEntityAnimator extends EntityAnimator {

    private final TextureRegion texture;

    public StaticEntityAnimator(Entity entity, TextureRegion texture) {
        super(entity);
        this.texture = texture;
    }

    @Override
    public void draw(SpriteBatch batch) {
        Vector2 loc = entity.getLocation();
        float angle = entity.getAngle();
        batch.draw(texture, loc.x - w/2, loc.y - h/2, w/2, h/2, w, h, 1, 1, (float) (angle*180/Math.PI));
    }
}
