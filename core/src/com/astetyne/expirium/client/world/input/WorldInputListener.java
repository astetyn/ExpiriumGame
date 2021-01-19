package com.astetyne.expirium.client.world.input;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.screens.GameScreen;
import com.astetyne.expirium.client.world.GameWorld;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;

public class WorldInputListener extends InputAdapter {

    private final GameWorld world;
    private boolean pressed;

    public WorldInputListener(GameWorld world) {
        this.world = world;
        pressed = false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(!GameScreen.get().getActiveRoot().canInteractWithWorld()) return false;
        Vector3 vec = world.getCamera().unproject(new Vector3(screenX, screenY, 0));
        if(vec.x < 0 || vec.x >= world.getTerrainWidth() || vec.y < 0 || vec.y >= world.getTerrainHeight()) {
            return false;
        }
        System.out.println(vec.x+" "+vec.y);
        ExpiGame.get().getClientGateway().getManager().putInteractPacket(vec.x, vec.y, InteractType.PRESS);
        pressed = true;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(!pressed) return true;
        pressed = false;
        Vector3 vec = world.getCamera().unproject(new Vector3(screenX, screenY, 0));
        ExpiGame.get().getClientGateway().getManager().putInteractPacket(vec.x, vec.y, InteractType.RELEASE);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(!pressed) return false;
        Vector3 vec = world.getCamera().unproject(new Vector3(screenX, screenY, 0));
        if(vec.x < 0 || vec.x >= world.getTerrainWidth() || vec.y < 0 || vec.y >= world.getTerrainHeight()) {
            return true;
        }
        ExpiGame.get().getClientGateway().getManager().putInteractPacket(vec.x, vec.y, InteractType.DRAG);
        return true;
    }

}
