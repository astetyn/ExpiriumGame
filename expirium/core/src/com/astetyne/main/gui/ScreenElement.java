package com.astetyne.main.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class ScreenElement {

    protected final int xRatio, yRatio, width, height;
    protected int x, y;

    public ScreenElement(int xRatio, int yRatio, int width, int height) {
        this.xRatio = xRatio;
        this.yRatio = yRatio;
        this.width = width;
        this.height = height;
        resize();
    }

    public abstract void update();

    public abstract void render(SpriteBatch batch);

    public void resize() {
        x = (int)((Gdx.graphics.getWidth()/100.0f) * xRatio);
        y = (int)((Gdx.graphics.getHeight()/100.0f) * yRatio);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
