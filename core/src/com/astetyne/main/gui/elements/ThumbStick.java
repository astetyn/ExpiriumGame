package com.astetyne.main.gui.elements;

import com.astetyne.main.TextureManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class ThumbStick extends Widget {

    private int circleX, circleY;
    private float radius;
    private float xR, yR;
    private boolean touched;

    public ThumbStick() {

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
            int xi = Gdx.input.getX();
            int yi = Gdx.input.getY();

            Vector2 touch = new Vector2(xi - getX() - radius, Gdx.graphics.getHeight() - yi- getY() - getHeight()/2.0f);
            float angle = touch.angleDeg();
            float radiusMin = Math.min(touch.len(), radius);
            circleX = (int) (getX()+getWidth()/2.0+Math.cos(Math.toRadians(angle))*radiusMin);
            circleY = (int) (getY()+getHeight()/2.0+Math.sin(Math.toRadians(angle))*radiusMin);
            xR = (circleX - (getX()+radius)) / (radius);
            yR = (circleY - (getY()+getHeight()/2.0f)) / (getHeight()/2.0f);
        }

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(TextureManager.THUMB_STICK_DOWN, getX(), getY(), getWidth(), getHeight());
        batch.draw(TextureManager.THUMB_STICK_UP, circleX - getWidth()/4.0f, circleY - getHeight()/4.0f, radius, getHeight()/2.0f);
    }

    @Override
    public void sizeChanged() {
        super.sizeChanged();
        radius = getWidth()/2.0f;
        circleX = (int) (getX()+getWidth()/2);
        circleY = (int) (getY()+getWidth()/2);
    }

    public float getHorz() {
        return xR;
    }

    public float getVert() {
        return yR;
    }
}
