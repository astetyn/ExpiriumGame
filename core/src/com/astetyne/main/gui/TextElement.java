package com.astetyne.main.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;

public class TextElement extends ScreenElement {

    private String text;
    private BitmapFont bf;

    public TextElement(int xRatio, int yRatio, int heightRatio, String text) {
        super(xRatio, yRatio+(int)(1.5f*heightRatio), 0, heightRatio);
        this.text = text;
        bf = new BitmapFont(Gdx.files.internal("lolfont.fnt"));
    }

    public TextElement(int xRatio, int yRatio, String text, float size) {
        super(xRatio, yRatio + (int)(size*10f), 0,0);
        this.text = text;
        bf = new BitmapFont(Gdx.files.internal("lolfont.fnt"));
        bf.getData().setScale(size);
    }

    @Override
    public void update() {

    }

    @Override
    public void render(SpriteBatch batch) {
        bf.draw(batch, text, x, y);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
