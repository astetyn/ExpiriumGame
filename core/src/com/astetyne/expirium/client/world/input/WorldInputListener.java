package com.astetyne.expirium.client.world.input;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.screens.GameScreen;
import com.astetyne.expirium.client.world.GameWorld;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class WorldInputListener extends InputAdapter implements GestureDetector.GestureListener {

    private final GestureDetector detector;
    private final GameWorld world;
    private boolean pressed;

    public WorldInputListener(GameWorld world) {
        this.world = world;
        pressed = false;
        detector = new GestureDetector(this);
    }

    // from input adapter
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        detector.touchDown(screenX, screenY, pointer, button);

        if(!GameScreen.get().getActiveRoot().canInteractWithWorld()) return false;
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
        detector.touchUp(screenX, screenY, pointer, button);

        if(!pressed) return true;
        pressed = false;
        Vector3 vec = world.getCamera().unproject(new Vector3(screenX, screenY, 0));
        ExpiGame.get().getClientGateway().getManager().putInteractPacket(vec.x, vec.y, InteractType.RELEASE);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        detector.touchDragged(screenX, screenY, pointer);

        if(!pressed) return false;
        Vector3 vec = world.getCamera().unproject(new Vector3(screenX, screenY, 0));
        if(vec.x < 0 || vec.x >= world.getTerrainWidth() || vec.y < 0 || vec.y >= world.getTerrainHeight()) {
            return true;
        }
        ExpiGame.get().getClientGateway().getManager().putInteractPacket(vec.x, vec.y, InteractType.DRAG);
        return true;
    }

    // from gesture listener
    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        System.out.println("tap");
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
        System.out.println("zoom "+initialDistance + " "+distance);
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }
}
