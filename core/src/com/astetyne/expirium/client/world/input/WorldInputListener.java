package com.astetyne.expirium.client.world.input;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.screens.GameScreen;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.world.GameWorld;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class WorldInputListener extends InputAdapter implements GestureDetector.GestureListener {

    private final GestureDetector detector;
    private final GameWorld world;
    private boolean pressed;
    private float savedZoom;

    public WorldInputListener(GameWorld world) {
        this.world = world;
        pressed = false;
        detector = new GestureDetector(this);
        savedZoom = 1;
    }

    // from input adapter
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(!GameScreen.get().getActiveRoot().canInteractWithWorld()) return false;
        savedZoom = GameScreen.get().getWorld().getCamera().zoom;
        detector.touchDown(screenX, screenY, pointer, button);
        Vector3 vec = world.getCamera().unproject(new Vector3(screenX, screenY, 0));
        if(vec.x < 0 || vec.x >= world.getTerrainWidth() || vec.y < 0 || vec.y >= world.getTerrainHeight()) {
            return false;
        }
        ExpiGame.get().getClientGateway().getManager().putInteractPacket(vec.x, vec.y, InteractType.PRESS);
        pressed = true;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(!GameScreen.get().getActiveRoot().canInteractWithWorld()) return false;
        detector.touchUp(screenX, screenY, pointer, button);
        if(!pressed) return true;
        pressed = false;
        Vector3 vec = world.getCamera().unproject(new Vector3(screenX, screenY, 0));
        vec.x = Math.max(vec.x, 0);
        vec.y = Math.max(vec.y, 0);
        vec.x = Math.min(vec.x, world.getTerrainWidth() - 0.0001f);
        vec.y = Math.min(vec.y, world.getTerrainHeight() - 0.0001f);
        ExpiGame.get().getClientGateway().getManager().putInteractPacket(vec.x, vec.y, InteractType.RELEASE);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(!GameScreen.get().getActiveRoot().canInteractWithWorld()) return false;
        detector.touchDragged(screenX, screenY, pointer);
        if(!pressed) return false;

        boolean firstTouch = false;
        for(int i = 0; i < 20; i++) {
            if(Gdx.input.isTouched(i)) {
                if(firstTouch) return false;
                firstTouch = true;
            }
        }

        Vector3 vec = world.getCamera().unproject(new Vector3(screenX, screenY, 0));
        if(vec.x < 0 || vec.x >= world.getTerrainWidth() || vec.y < 0 || vec.y >= world.getTerrainHeight()) {
            return true;
        }
        ExpiGame.get().getClientGateway().getManager().putInteractPacket(vec.x, vec.y, InteractType.DRAG);
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if(!Consts.DEBUG) return false;
        // just for desktop development testing ----
        OrthographicCamera cam = GameScreen.get().getWorld().getCamera();
        cam.zoom += amountY*0.1;
        cam.update();
        return true;
    }

    // from gesture listener
    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        if(!GameScreen.get().getPlayerData().getThumbStickData1().isNeutral() ||
                !GameScreen.get().getPlayerData().getThumbStickData2().isNeutral()) return false;
        OrthographicCamera cam = GameScreen.get().getWorld().getCamera();
        if(savedZoom * (initialDistance / distance) * cam.viewportWidth >= world.getTerrainWidth() || savedZoom * (initialDistance / distance) * cam.viewportHeight >= world.getTerrainHeight()) {
            return false;
        }
        cam.zoom = Math.min(savedZoom * (initialDistance / distance), 3);
        cam.zoom = Math.max(cam.zoom, 0.2f);
        cam.update();
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {}
}
