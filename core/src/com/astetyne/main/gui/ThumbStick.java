package com.astetyne.main.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class ThumbStick extends Widget {

    private final ThumbStickStyle style;

    private int circleX, circleY;
    private float radius;
    private float xR, yR;
    private boolean touched;
    private int touchID;

    public ThumbStick(ThumbStickStyle style) {

        //todo: nastavit alpha pri focused a bez, pokusit sa to pridat do skinu, tazenie tukanim prsta?

        this.style = style;

        xR = 0;
        yR = 0;
        touched = false;

        addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                radius = getWidth()/2.0f;
                circleX = (int) (getX()+getWidth()/2);
                circleY = (int) (getY()+getWidth()/2);
                touched = true;
                touchID = pointer;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                circleX = (int) (getX()+getWidth()/2);
                circleY = (int) (getY()+getHeight()/2);
                xR = 0;
                yR = 0;
                touched = false;
            }

        });

    }

    @Override
    public void act(float delta) {

        super.act(delta);

        if(touched) {
            int xi = Gdx.input.getX(touchID);
            int yi = Gdx.input.getY(touchID);

            Vector2 touch = new Vector2(xi - getX() - radius, Gdx.graphics.getHeight() - yi- getY() - getHeight()/2.0f);
            float angle = touch.angleDeg();
            float radiusMin = Math.min(touch.len(), radius);
            circleX = (int) (getX()+getWidth()/2.0+Math.cos(Math.toRadians(angle))*radiusMin);
            circleY = (int) (getY()+getHeight()/2.0+Math.sin(Math.toRadians(angle))*radiusMin);
            xR = (circleX - (getX()+radius)) / (radius);
            yR = (circleY - (getY()+getHeight()/2.0f)) / (getHeight()/2.0f);
        }else {
            radius = getWidth()/2.0f;
            circleX = (int) (getX()+getWidth()/2);
            circleY = (int) (getY()+getWidth()/2);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(touched) {
            batch.setColor(1,1,1,0.8f);
        }else {
            batch.setColor(1,1,1,0.4f);
        }
        style.background.draw(batch, getX(), getY(), getWidth(), getHeight());
        style.foreground.draw(batch, circleX - getWidth()/4.0f, circleY - getHeight()/4.0f, radius, getHeight()/2.0f);
        batch.setColor(Color.WHITE);
    }

    @Override
    public float getPrefWidth() {
        return 150 * Gdx.graphics.getDensity();
    }

    @Override
    public float getPrefHeight() {
        return 150 * Gdx.graphics.getDensity();
    }

    public float getHorz() {
        return xR;
    }

    public float getVert() {
        return yR;
    }

    public static class ThumbStickStyle {

        final Drawable background;
        final Drawable foreground;

        public ThumbStickStyle(Drawable background, Drawable foreground) {
            this.background = background;
            this.foreground = foreground;
        }
    }
}
