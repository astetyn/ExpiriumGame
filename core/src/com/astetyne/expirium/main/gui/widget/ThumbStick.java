package com.astetyne.expirium.main.gui.widget;

import com.astetyne.expirium.main.data.ThumbStickData;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ThumbStick extends Widget {

    private final ThumbStickStyle style;
    private final ThumbStickData data;

    private float circleX, circleY;
    private float radius;
    private boolean touched;
    private int touchID;
    private final Vector2 touchVec;

    public ThumbStick(ThumbStickData data, ThumbStickStyle style) {

        this.data = data;

        touchVec = new Vector2();

        this.style = style;
        touched = false;

        addListener(new ClickListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                touched = true;
                touchID = pointer;
                recalculate(getX() + x, getY() + y);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                returnToDefault();
                touched = false;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                recalculate(getX() + x, getY() + y);
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
        circleY = (int) (getY()+getHeight()/2);
        data.horz = 0;
        data.vert = 0;
    }

    private void recalculate(float xi, float yi) {

        touchVec.set(xi - getX() - radius, yi- getY() - getHeight()/2.0f);
        float angle = touchVec.angleDeg();
        float radiusMaxX = Math.min(touchVec.len(), radius);
        float radiusMaxY = Math.min(touchVec.len(), getHeight()/2);
        circleX = getX()+getWidth()/2.0f+(float)Math.cos(Math.toRadians(angle))*radiusMaxX;
        circleY = getY()+getHeight()/2.0f+(float)Math.sin(Math.toRadians(angle))*radiusMaxY;
        data.horz = (circleX - (getX()+ radius)) / (radius);
        data.vert = (circleY - (getY()+getHeight()/2.0f)) / (getHeight()/2.0f);

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        validate();
        if(touched) {
            batch.setColor(1,1,1,0.8f);
        }else {
            batch.setColor(1,1,1,0.4f);
        }
        batch.draw(style.background, getX(), getY(), getWidth(), getHeight());
        batch.draw(style.foreground, circleX - getWidth()/4.0f, circleY - getHeight()/4.0f, radius, getHeight()/2.0f);
        batch.setColor(Color.WHITE);
    }

    public static class ThumbStickStyle {

        final TextureRegion background, foreground;

        public ThumbStickStyle(TextureRegion background, TextureRegion foreground) {
            this.background = background;
            this.foreground = foreground;
        }
    }
}
