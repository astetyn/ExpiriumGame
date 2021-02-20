package com.astetyne.expirium.client.gui.widget;

import com.astetyne.expirium.client.data.ThumbStickData;
import com.astetyne.expirium.client.utils.Consts;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MoveThumbStick extends Widget {

    private final MoveThumbStickStyle style;
    private final ThumbStickData data;

    private float circleX, circleY;
    private float maxRadiusX, maxRadiusY;
    private float circleWidth, circleHeight;
    private boolean touched;
    private int touchID;
    private final Vector2 touchVec;

    public MoveThumbStick(ThumbStickData data, MoveThumbStickStyle style) {

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
                touched = false;
                returnToDefault();
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
        circleWidth = getWidth()/2;
        circleHeight = getHeight()/2;
        maxRadiusX = getWidth()/3f;
        maxRadiusY = getHeight()/3f;
        circleX = (int) (getX()+getWidth()/2);
        circleY = (int) (getY()+getHeight()/4);
        data.horz = 0;
        data.vert = 0;
    }

    private void recalculate(float xi, float yi) {

        touchVec.set(xi - getX() - circleWidth, yi - getY() - circleHeight/2);
        float angle = touchVec.angleRad();
        float radiusX = Math.min(touchVec.len(), maxRadiusX);
        float radiusY = Math.min(touchVec.len(), maxRadiusY);
        circleX = getX()+getWidth()/2f+(float)Math.cos(angle)*radiusX;
        circleY = getY()+circleHeight/2+(float)Math.max(Math.sin(angle)*radiusY, 0);
        data.horz = (circleX - (getX() + getWidth()/2)) / maxRadiusX;
        data.vert = (circleY - (getY() + circleHeight/2)) / maxRadiusY;

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        validate();

        if(!((data.horz < 0 && data.vert < Consts.JUMP_THRESHOLD) ||
                (data.vert >= Consts.JUMP_THRESHOLD && data.horz <= -Consts.HORZ_JUMP_THRESHOLD))) batch.setColor(1,1,1,0.4f);
        batch.draw(style.sideArrow, getX(), getY(), getWidth()/8, 0, getWidth()/4, getHeight()/2, -1, 1, 0);
        batch.setColor(Color.WHITE);

        if(!((data.horz > 0 && data.vert < Consts.JUMP_THRESHOLD) ||
                (data.vert >= Consts.JUMP_THRESHOLD && data.horz >= Consts.HORZ_JUMP_THRESHOLD))) batch.setColor(1,1,1,0.4f);
        batch.draw(style.sideArrow, getX()+3*getWidth()/4, getY(), getWidth()/4, getHeight()/2);
        batch.setColor(Color.WHITE);

        if(!(data.vert >= Consts.JUMP_THRESHOLD)) batch.setColor(1,1,1,0.4f);
        batch.draw(style.upArrow, getX()+getWidth()/3, getY()+getHeight()/2, getWidth()/3, getHeight()/4);
        batch.setColor(Color.WHITE);

        if(!touched) batch.setColor(1,1,1,0.4f);
        batch.draw(style.frontCircle, circleX - circleWidth/2, circleY - circleHeight/2, circleWidth, circleHeight);
        batch.setColor(Color.WHITE);
    }

    public static class MoveThumbStickStyle {

        final TextureRegion frontCircle, sideArrow, upArrow;

        public MoveThumbStickStyle(TextureRegion frontCircle, TextureRegion sideArrow, TextureRegion upArrow) {
            this.frontCircle = frontCircle;
            this.sideArrow = sideArrow;
            this.upArrow = upArrow;
        }
    }

}
