package com.astetyne.expirium.client.resources;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Locale;

public enum PlayerCharacter {

    FENDER,
    AMANDA,
    ;

    private Animation<TextureRegion> idleAnim, moveAnim, interactAnim;
    private TextureRegion thumbnail;

    public static void loadTextures(TextureAtlas atlas) {
        for(PlayerCharacter pa : values()) {
            String name = "char_"+pa.name().toLowerCase(Locale.US);
            pa.idleAnim = new Animation<>(0.5f, atlas.findRegions(name+"_idle"), Animation.PlayMode.LOOP_PINGPONG);
            pa.moveAnim = new Animation<>(0.05f, atlas.findRegions(name+"_move"), Animation.PlayMode.LOOP_PINGPONG);
            pa.interactAnim = new Animation<>(0.12f, atlas.findRegions(name+"_interact"), Animation.PlayMode.LOOP);
            pa.thumbnail = atlas.findRegion(name+"_thumbnail");
        }
    }

    public static PlayerCharacter get(int i) {
        return values()[i];
    }

    public Animation<TextureRegion> getIdleAnim() {
        return idleAnim;
    }

    public Animation<TextureRegion> getMoveAnim() {
        return moveAnim;
    }

    public Animation<TextureRegion> getInteractAnim() {
        return interactAnim;
    }

    public TextureRegion getThumbnail() {
        return thumbnail;
    }
}
