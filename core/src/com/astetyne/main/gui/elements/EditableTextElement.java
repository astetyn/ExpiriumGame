package com.astetyne.main.gui.elements;

import com.astetyne.main.ExpiriumGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class EditableTextElement extends TextElement implements InputProcessor {

    public EditableTextElement(int xRatio, int yRatio, int width, String text, float size) {
        super(xRatio, yRatio, width, text, size);
        ExpiriumGame.getGame().getGui().getInputMultiplexer().addProcessor(this);
    }

    @Override
    public void render(SpriteBatch batch) {
        if(focused) {
            bf.setColor(Color.GREEN);
        }else {
            bf.setColor(Color.BLACK);
        }
        super.render(batch);
    }

    @Override
    public boolean keyDown(int keycode) {

        if(!focused) return false;

        if(keycode == Input.Keys.BACKSPACE && text.length() != 0) {
            setText(text.substring(0, text.length()-1));
            return true;
        }else if(keycode == Input.Keys.SPACE) {
            setText(text + " ");
            return true;
        }

        setText(text + getChar(keycode));
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    private String getChar(int keyCode) {

        int c = 0;

        // letters
        if(keyCode >= 29 && keyCode <= 54) {
            c = keyCode + 68;
            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) c -= 32;
        }

        //todo: numbers

        //todo: special characters

        if(c == 0) {
            return "";
        }else {
            return Character.toString((char)c);
        }

    }
}
