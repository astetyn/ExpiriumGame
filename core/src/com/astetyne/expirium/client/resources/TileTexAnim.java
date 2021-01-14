package com.astetyne.expirium.client.resources;

import com.astetyne.expirium.client.ExpiGame;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public enum TileTexAnim implements Textureable {

    CAMPFIRE_BIG("campfire_big", 0.1f, Animation.PlayMode.LOOP_PINGPONG),
    CAMPFIRE_SMALL("campfire_small", 0.1f, Animation.PlayMode.LOOP_PINGPONG),

    TILE_BREAK("tile_break", 0.26f, Animation.PlayMode.LOOP);

    private final String regionName;
    private Animation<TextureRegion> anim;
    private final float interval;
    private final Animation.PlayMode playMode;

    TileTexAnim(String regionName, float interval, Animation.PlayMode playMode) {
        this.regionName = regionName;
        this.interval = interval;
        this.playMode = playMode;
    }

    public static void loadTextures() {
        TextureAtlas world = new TextureAtlas("world.atlas");
        for(TileTexAnim tta : values()) {
            tta.anim = new Animation<>(tta.interval, world.findRegions(tta.regionName), tta.playMode);
        }
    }

    public TextureRegion getTex() {
        return anim.getKeyFrame(ExpiGame.get().getTimeSinceStart());
    }

    public Animation<TextureRegion> getAnim() {
        return anim;
    }

}
