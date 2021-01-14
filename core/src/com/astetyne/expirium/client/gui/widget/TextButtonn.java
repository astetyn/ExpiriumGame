package com.astetyne.expirium.client.gui.widget;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class TextButtonn extends Widget {

    private TextureRegion background;
    private BitmapFont font;

    /*public TextButton(TextureRegion background, BitmapFont font) {
        this.background = background;
        this.font = font;
    }*/

    public void draw(Batch batch, float parentAlpha) {
        batch.draw(background, getX(), getY(), getWidth(), getHeight());
    }

}
