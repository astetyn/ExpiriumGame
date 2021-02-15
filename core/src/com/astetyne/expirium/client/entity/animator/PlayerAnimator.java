package com.astetyne.expirium.client.entity.animator;

import com.astetyne.expirium.client.Res;
import com.astetyne.expirium.client.entity.EntityType;
import com.astetyne.expirium.client.entity.Player;
import com.astetyne.expirium.client.items.Item;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

public class PlayerAnimator extends MoveableEntityAnimator {

    private long lastInteractTime;
    private final Animation<TextureRegion> interactAnim;
    private final float xOff, yOff, baseAngle;

    public PlayerAnimator(Player p, Animation<TextureRegion> idleAnim, Animation<TextureRegion> moveAnim) {
        super(p, idleAnim, moveAnim);
        interactAnim = Res.PLAYER_INTERACT_ANIM;
        lastInteractTime = 0;
        xOff = 0.22f;
        yOff = 0.35f;
        baseAngle = -45;
    }

    public void draw(SpriteBatch batch) {

        Vector2 loc = entity.getLocation();

        Item itemInHand = ((Player) entity).getItemInHand();
        TextureRegion tex = itemInHand.getGridTexture();
        float itemW = itemInHand.getGridWidth() / 3f;
        float itemH = itemInHand.getGridHeight() / 3f;
        float playerW = EntityType.PLAYER.getWidth();

        if(lastInteractTime + (long)(interactAnim.getAnimationDuration()*1000) > System.currentTimeMillis() && timer <= interactAnim.getAnimationDuration()) {
            if(entity.isLookingRight()) {
                batch.draw(interactAnim.getKeyFrame(timer), loc.x, loc.y - yOffset, 0, 0, w, h, 1, 1, 1);
            }else {
                batch.draw(interactAnim.getKeyFrame(timer), loc.x + w, loc.y - yOffset, 0, 0, w, h, -1, 1, 1);
            }

            if(tex == null) return;
            float delta = (float)interactAnim.getKeyFrameIndex(timer) / interactAnim.getKeyFrames().length;

            if(entity.isLookingRight()) {

                Vector2 tempStart = new Vector2(loc.x + xOff + 0.3f, loc.y + yOff + 0.2f);
                Vector2 tempEnd = new Vector2(loc.x + xOff, loc.y + yOff);
                tempStart.lerp(tempEnd, delta);
                float angle = Interpolation.linear.apply(0, baseAngle, delta);

                batch.draw(tex, tempStart.x, tempStart.y, itemW/2, itemH/2, itemW, itemH, 1, 1, angle);

            }else {

                Vector2 tempStart = new Vector2(loc.x + playerW - xOff - itemW - 0.3f, loc.y + yOff + 0.2f);
                Vector2 tempEnd = new Vector2(loc.x + playerW - xOff - itemW, loc.y + yOff);
                tempStart.lerp(tempEnd, delta);
                float angle = Interpolation.linear.apply(0, -baseAngle, delta);

                batch.draw(tex, tempStart.x, tempStart.y, itemW/2, itemH/2, itemW, itemH, 1, 1, angle);
            }

        }else {
            super.draw(batch);

            if(tex == null) return;
            if(entity.isLookingRight()) {
                batch.draw(tex, loc.x + xOff, loc.y + yOff, itemW/2, itemH/2, itemW, itemH, 1, 1, baseAngle);
            }else {
                batch.draw(tex, loc.x + playerW - xOff - itemW, loc.y + yOff, itemW/2, itemH/2, itemW, itemH, -1, 1, -baseAngle);
            }
        }
    }

    public void handInteract() {
        lastInteractTime = System.currentTimeMillis();
        timer = 0;
    }

}
