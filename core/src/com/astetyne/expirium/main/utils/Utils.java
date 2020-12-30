package com.astetyne.expirium.main.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Utils {

    private static final GlyphLayout glyphLayout;

    static {
        glyphLayout = new GlyphLayout();
    }

    public static int sizeof(Object obj) {

        try {

            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);

            objectOutputStream.writeObject(obj);
            objectOutputStream.flush();
            objectOutputStream.close();


            return byteOutputStream.toByteArray().length;
        }catch(IOException ignored) {
            ignored.printStackTrace();
        }
        return -1;
    }

    public static float getTextWidth(String text, BitmapFont font) {
        glyphLayout.setText(font, text);
        return glyphLayout.width;
    }

    public static float getTextHeight(String text, BitmapFont font) {
        glyphLayout.setText(font, text);
        return glyphLayout.height;
    }

    public static float fromCMToPercW(float cm) {
        return (cm * Gdx.graphics.getDensity() * 50) / Gdx.graphics.getWidth() * 1000;
    }

    public static float percFromW(float val) {
        return Gdx.graphics.getWidth() / (float)Gdx.graphics.getHeight() * val;
    }

    public static float percFromH(float val) {
        return Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth() * val;
    }

}
