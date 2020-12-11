package com.astetyne.main.gui.elements;

import com.astetyne.main.gui.elements.ScreenElement;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TextElement extends ScreenElement {

    protected String text;
    protected final BitmapFont bf;

    private int textShift;

    public TextElement(int xRatio, int yRatio, int width, String text, float size) {
        this(xRatio, yRatio, width, text, size, new BitmapFont(Gdx.files.internal("lolfont.fnt")));
    }

    public TextElement(int xRatio, int yRatio, int width, String text, float size, BitmapFont font) {
        super(xRatio, yRatio, width,0);
        this.text = text;
        textShift = 0;
        bf = font;
        bf.getData().setScale(size);
        updateTextWidth();
    }

    @Override
    public void update() {}

    @Override
    public void render(SpriteBatch batch) {
        bf.draw(batch, text, x, y+textShift);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        updateTextWidth();
    }

    private void updateTextWidth() {
        GlyphLayout gl = new GlyphLayout();
        gl.setText(bf, text);
        resizeElement(width, (int) gl.height);
        textShift = (int) gl.height;
        resize();
    }
}
