package com.astetyne.expirium.client.world.input;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.resources.TileTex;
import com.astetyne.expirium.client.screens.GameScreen;
import com.astetyne.expirium.client.tiles.Tile;
import com.astetyne.expirium.client.tiles.TileType;
import com.astetyne.expirium.client.world.GameWorld;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class TilePlacer implements InputProcessor {

    private final SpriteBatch batch;
    private final GameWorld world;
    private boolean pressed;

    public TilePlacer() {

        batch = ExpiGame.get().getBatch();
        world = GameScreen.get().getWorld();

    }

    public void render(Tile t, int x, int y) {

        if(t.getTypeFront() == TileType.AIR) return;

        if(GameScreen.get().isBuildViewActive()) {

            if(t.getStability() == 1) {
                batch.setColor(0.9f, 0f, 0f, 1);
            }else if(t.getStability() == 2) {
                batch.setColor(0.9f, 0.3f, 0f, 1);
            }else if(t.getStability() == 3) {
                batch.setColor(0.9f, 0.6f, 0f, 1);
            }else if(t.getStability() == 4) {
                batch.setColor(0.9f, 0.9f, 0f, 1);
            }
            batch.draw(TileTex.WHITE_TILE.getTex(), x, y, 1, 1);
            batch.setColor(1, 1, 1, 1);
        }else {
            batch.draw(t.getTypeFront().getTex(), x, y, 1, 1);
        }

    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
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
        if(!GameScreen.get().getActiveRoot().canInteractWithWorld()) return false;
        Vector3 vec = world.getCamera().unproject(new Vector3(screenX, screenY, 0));
        if(vec.x < 0 || vec.x >= world.getTerrainWidth() && vec.y < 0 || vec.y >= world.getTerrainHeight()) {
            return false;
        }
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
        if(vec.x < 0 || vec.x >= world.getTerrainWidth() && vec.y < 0 || vec.y >= world.getTerrainHeight()) {
            return true;
        }
        ExpiGame.get().getClientGateway().getManager().putInteractPacket(vec.x, vec.y, InteractType.DRAG);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

}
