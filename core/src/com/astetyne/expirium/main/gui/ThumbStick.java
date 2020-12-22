package com.astetyne.expirium.main.gui;

import com.astetyne.expirium.main.stages.GameStage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class ThumbStick extends Widget {

    private final ThumbStickStyle style;

    private int circleX, circleY;
    private float radius;
    private float xR, yR, angle;
    private boolean touched;
    private int touchID;

    public ThumbStick(ThumbStickStyle style) {

        this.style = style;
        touched = false;
        returnToDefault();

        addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                touched = true;
                touchID = pointer;
                recalculate();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                returnToDefault();
                touched = false;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                recalculate();
            }
        });
    }

    @Override
    public void layout() {
        super.layout();
        returnToDefault();
    }

    private void returnToDefault() {
        radius = getWidth()/2.0f;
        circleX = (int) (getX()+getWidth()/2);
        circleY = (int) (getY()+getWidth()/2);
        xR = 0;
        yR = 0;
    }

    private void recalculate() {

        int xi = Gdx.input.getX(touchID);
        int yi = Gdx.input.getY(touchID);

        Vector2 touch = new Vector2(xi - getX() - radius, Gdx.graphics.getHeight() - yi- getY() - getHeight()/2.0f);
        angle = touch.angleDeg();
        float radiusMin = Math.min(touch.len(), radius);
        circleX = (int) (getX()+getWidth()/2.0+Math.cos(Math.toRadians(angle))*radiusMin);
        circleY = (int) (getY()+getHeight()/2.0+Math.sin(Math.toRadians(angle))*radiusMin);
        xR = (circleX - (getX()+radius)) / (radius);
        yR = (circleY - (getY()+getHeight()/2.0f)) / (getHeight()/2.0f);

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(touched) {
            batch.setColor(1,1,1,0.8f);
        }else {
            batch.setColor(1,1,1,0.4f);
        }
        batch.draw(style.background, getX(), getY(), getWidth(), getHeight());
        batch.draw(style.foreground, circleX - getWidth()/4.0f, circleY - getHeight()/4.0f, radius, getHeight()/2.0f);
        batch.setColor(Color.WHITE);
    }

    @Override
    public float getPrefWidth() {
        return GameStage.toPixels(150);
    }

    @Override
    public float getPrefHeight() {
        return GameStage.toPixels(150);
    }

    public float getHorz() {
        return xR;
    }

    public float getVert() {
        return yR;
    }

    public float getAngle() {
        return angle;
    }

    public static class ThumbStickStyle {

        final TextureRegion background, foreground;

        public ThumbStickStyle(TextureRegion background, TextureRegion foreground) {
            this.background = background;
            this.foreground = foreground;
        }
    }
}
