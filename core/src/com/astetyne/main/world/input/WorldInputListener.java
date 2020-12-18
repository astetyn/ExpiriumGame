package com.astetyne.main.world.input;

import com.astetyne.main.stages.GameStage;
import com.astetyne.main.utils.Constants;
import com.astetyne.main.world.GameWorld;
import com.astetyne.main.world.TileType;
import com.astetyne.main.world.tiles.Tile;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;

public class WorldInputListener implements InputProcessor {

    private final GameWorld world;
    private int validPointer;

    public WorldInputListener() {
        this.world = GameStage.get().getWorld();
        validPointer = -1;
    }

    public void update() {
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

    private Tile getTileAtFromScreen(int screenX, int screenY) {

        Vector3 vec =  world.getCamera().unproject(new Vector3(screenX, screenY, 0));
        int blockX = (int) (vec.x / GameWorld.PPM);
        int blockY = (int) (vec.y / GameWorld.PPM);

        int w = Constants.T_W_CH;
        int h = Constants.T_H_CH;

        Tile t = world.getTileAt(blockX, blockY);

        if(t.getType() == TileType.AIR) return null;
        return t;
    }

}
