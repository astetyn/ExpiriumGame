package com.astetyne.expirium.client.resources;

import com.astetyne.expirium.client.ExpiGame;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Locale;

public enum TileTexAnim implements Textureable {

    CAMPFIRE_BIG(0.1f, Animation.PlayMode.LOOP_PINGPONG),
    CAMPFIRE_SMALL(0.1f, Animation.PlayMode.LOOP_PINGPONG),

    /*WATER_1(0.5f, Animation.PlayMode.LOOP_PINGPONG),
    WATER_2(0.5f, Animation.PlayMode.LOOP_PINGPONG),
    WATER_3(0.5f, Animation.PlayMode.LOOP_PINGPONG),
    WATER_4(0.5f, Animation.PlayMode.LOOP_PINGPONG),*/
    WATER_5(0.5f, Animation.PlayMode.LOOP_PINGPONG),
    
    TILE_BREAK(0.26f, Animation.PlayMode.LOOP);

    private Animation<TextureRegion> anim;
    private final float interval;
    private final Animation.PlayMode playMode;

    TileTexAnim(float interval, Animation.PlayMode playMode) {
        this.interval = interval;
        this.playMode = playMode;
    }

    public static void loadTextures(TextureAtlas atlas) {
        for(TileTexAnim tta : values()) {
            tta.anim = new Animation<>(tta.interval, atlas.findRegions(tta.name().toLowerCase(Locale.US)), tta.playMode);
        }
    }

    public TextureRegion getTex() {
        return anim.getKeyFrame(ExpiGame.get().getTimeSinceStart());
    }

    public Animation<TextureRegion> getAnim() {
        return anim;
    }

}
