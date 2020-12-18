package com.astetyne.main.gui;

import com.astetyne.main.stages.GameStage;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class SwitchArrow extends Widget {

    private final SwitchArrowStyle style;
    private boolean pressed;

    public SwitchArrow(SwitchArrowStyle style, Runnable onClick) {

        this.style = style;
        pressed = false;

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                onClick.run();
                pressed = true;
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                pressed = false;
            }
        });

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(pressed) {
            batch.draw(style.pressed, getX(), getY(), getWidth(), getHeight());
        }else {
            batch.draw(style.unpressed, getX(), getY(), getWidth(), getHeight());
        }
    }

    @Override
    public float getPrefWidth() {
        return GameStage.toPixels(50);
    }

    @Override
    public float getPrefHeight() {
        return GameStage.toPixels(30);
    }

    public static class SwitchArrowStyle {

        final TextureRegion unpressed, pressed;

        public SwitchArrowStyle(TextureRegion unpressed, TextureRegion pressed) {
            this.unpressed = unpressed;
            this.pressed = pressed;
        }
    }

}
