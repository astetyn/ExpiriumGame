package com.astetyne.main.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

public class GUIManager {

    private final OrthographicCamera guiCam;
    private final List<ScreenElement> elements;

    public GUIManager() {
        guiCam = new OrthographicCamera();
        elements = new ArrayList<>();
    }

    public void update() {
        for(ScreenElement element : elements) {
            element.update();
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

    public List<ScreenElement> getElements() {
        return elements;
    }

}
