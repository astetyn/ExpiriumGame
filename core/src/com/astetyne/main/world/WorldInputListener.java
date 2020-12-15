package com.astetyne.main.world;

import com.astetyne.main.Constants;
import com.astetyne.main.ExpiriumGame;
import com.astetyne.main.ResourceManager;
import com.astetyne.main.net.client.actions.TileBreakActionC;
import com.astetyne.main.world.tiles.Tile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class WorldInputListener implements InputProcessor {

    private final GameWorld world;
    private Tile targetTile;
    private float timeAccumulator;
    private int validPointer;

    public WorldInputListener(GameWorld world) {
        this.world = world;
        targetTile = null;
        validPointer = -1;
    }

    public void update() {

        if(targetTile == null) return;

        timeAccumulator += Gdx.graphics.getDeltaTime();

        if(timeAccumulator >= targetTile.getTileExtraData().getDurability()) {
            ExpiriumGame.getGame().getClientGateway().addAction(new TileBreakActionC(targetTile));
            timeAccumulator = 0;
            targetTile = null;
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

        if(targetTile != null) return false;

        Tile t = getTileAtFromScreen(screenX, screenY);
        if(t == null) return false;
        targetTile = t;
        validPointer = pointer;

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        timeAccumulator = 0;
        targetTile = null;
        validPointer = -1;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        if(validPointer != pointer) return false;

        Tile newTarget = getTileAtFromScreen(screenX, screenY);

        if(targetTile != null) {
            if(targetTile != newTarget) {
                timeAccumulator = 0;
                targetTile = newTarget;
            }
        }else if(newTarget != null){
            timeAccumulator = 0;
            targetTile = newTarget;
        }

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

        int w = Constants.TILES_WIDTH_CHUNK;
        int h = Constants.TILES_HEIGHT_CHUNK;

        if(blockX < 0 || blockX >= world.getChunks().length * w || blockY < 0 || blockY >= h) {
            return null;
        }
        int chunkID = blockX / Constants.TILES_WIDTH_CHUNK;
        blockX -= chunkID * Constants.TILES_WIDTH_CHUNK;
        WorldChunk chunk = world.getChunks()[chunkID];

        if(chunk == null) return null;
        Tile t = chunk.getTerrain()[blockY][blockX];
        if(t.getType() == TileType.AIR) return null;
        return t;
    }

    public void renderBreakingTile(SpriteBatch batch) {
        if(targetTile == null) return;

        float durability = timeAccumulator / targetTile.getTileExtraData().getDurability();
        int x = targetTile.getX() + Constants.TILES_WIDTH_CHUNK * targetTile.getChunk().getId();
        batch.draw(ResourceManager.TILE_BREAK_ANIM.getKeyFrame(durability), x, targetTile.getY(), 1, 1);
    }
}
