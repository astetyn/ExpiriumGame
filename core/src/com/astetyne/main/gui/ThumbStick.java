package com.astetyne.main.gui;

import com.astetyne.main.TextureManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class ThumbStick extends ScreenElement {

    private int circleX, circleY;
    private final float radius;
    private float xR, yR;
    private boolean pressed;

    public ThumbStick(int xRatio, int yRatio, int size) {
        super(xRatio, yRatio, size, size);
        radius = width/2.0f;
        circleX = x+width/2;
        circleY = y+height/2;
        xR = 0;
        yR = 0;
        pressed = false;
    }

    @Override
    public void update() {

        if(Gdx.input.isTouched()) {

            int xi = Gdx.input.getX();
            int yi = Gdx.input.getY();

            if(pressed) {

                Vector2 touch = new Vector2(xi - x - radius, Gdx.graphics.getHeight() - yi- y - height/2.0f);
                float angle = touch.angleDeg();
                float radiusMin = Math.min(touch.len(), radius);
                circleX = (int) (x+width/2.0+Math.cos(Math.toRadians(angle))*radiusMin);
                circleY = (int) (y+height/2.0+Math.sin(Math.toRadians(angle))*radiusMin);
                xR = (circleX - (x+radius)) / (radius);
                yR = (circleY - (y+height/2.0f)) / (height/2.0f);
                //System.out.println("X: "+x+" Y: "+y);

            }else if(xi > x && xi < x + width && Gdx.graphics.getHeight() - yi > y && Gdx.graphics.getHeight() - yi < y + height) {
                pressed = true;
            }
        }else {
            circleX = x+width/2;
            circleY = y+height/2;
            xR = 0;
            yR = 0;
            pressed = false;
        }

    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(TextureManager.THUMB_STICK_DOWN, x, y, width, height);
        batch.draw(TextureManager.THUMB_STICK_UP, circleX - width/4.0f, circleY - height/4.0f, radius, height/2.0f);
    }

    public float getXR() {
        return xR;
    }

    public float getYR() {
        return yR;
    }
}
