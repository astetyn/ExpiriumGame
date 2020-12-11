package com.astetyne.main.gui.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class ScreenElement {

    protected int xRatio, yRatio, width, height;
    protected int x, y;
    private Touchable touchable;
    protected boolean touched, focused;

    public ScreenElement(int xRatio, int yRatio, int width, int height) {
        this.xRatio = xRatio;
        this.yRatio = yRatio;
        this.width = width;
        this.height = height;
        touchable = new Touchable() {
            @Override
            public void onTouch() {}
            @Override
            public void onRelease() {}
            @Override
            public void onFocus() {}
            @Override
            public void onOutOfFocus() {}
        };
        touched = false;
        focused = false;
        resize();
    }

    public final void preUpdate() {
        checkTouch();
        update();
    }

    protected abstract void update();

    public abstract void render(SpriteBatch batch);

    public void resize() {
        x = (int)((Gdx.graphics.getWidth()/100.0f) * xRatio);
        y = (int)((Gdx.graphics.getHeight()/100.0f) * yRatio);
    }

    public void resizeElement(int newWidth, int newHeight) {
        width = newWidth;
        height = newHeight;
    }

    public void checkTouch() {
        if(Gdx.input.isTouched()) {
            int xi = Gdx.input.getX();
            int yi = Gdx.graphics.getHeight() - Gdx.input.getY();
            if(x <= xi && xi <= x+width && y <= yi  && yi <=  y+height) {
                if(!touched) {
                    touchable.onTouch();
                    touched = true;
                }
                if(!focused) {
                    touchable.onFocus();
                    focused = true;
                }
            }else if(focused){
                touchable.onOutOfFocus();
                focused = false;
            }
        }else if(touched) {
            touchable.onRelease();
            touched = false;
        }
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

    public void setTouchable(Touchable touchable) {
        this.touchable = touchable;
    }
}
