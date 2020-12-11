package com.astetyne.main.gui;

import com.astetyne.main.gui.elements.ScreenElement;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

public class GUIManager {

    private final OrthographicCamera guiCam;
    private final List<ScreenElement> elements;
    private final InputMultiplexer inputMultiplexer;

    public GUIManager() {
        guiCam = new OrthographicCamera();
        elements = new ArrayList<>();
        inputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    public void update() {
        for(ScreenElement element : elements) {
            element.preUpdate();
        }
    }

    public void render(SpriteBatch batch)  {

        batch.setProjectionMatrix(guiCam.combined);
        for(ScreenElement element : elements) {
            element.render(batch);
        }

    }

    public void onResize() {
        guiCam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        guiCam.update();
        for(ScreenElement element : elements) {
            element.resize();
        }
    }

    public void addElement(ScreenElement element) {
        elements.add(element);
    }

    public void removeElement(ScreenElement element) {
        if(element instanceof InputProcessor) {
            inputMultiplexer.removeProcessor((InputProcessor) element);
        }
        elements.remove(element);
    }

    public InputMultiplexer getInputMultiplexer() {
        return inputMultiplexer;
    }
}
