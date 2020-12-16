package com.astetyne.main.world;

import com.astetyne.main.Constants;
import com.astetyne.main.ExpiriumGame;
import com.astetyne.main.Resources;
import com.astetyne.main.gui.ThumbStick;
import com.astetyne.main.net.client.actions.TileBreakActionC;
import com.astetyne.main.stages.RunningGameStage;
import com.astetyne.main.world.tiles.Tile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class WorldInputListener implements InputProcessor {

    private final GameWorld world;
    private final RunningGameStage gameStage;
    private Tile targetTile;
    private float timeAccumulator;
    private int validPointer;
    private final ThumbStick breakTS;

    public WorldInputListener(RunningGameStage gameStage, GameWorld world) {
        this.gameStage = gameStage;
        this.world = world;
        breakTS = gameStage.getGameGUI().getBreakTS();
        targetTile = null;
        validPointer = -1;
        timeAccumulator = 0;
    }

    public void update() {

        // check if breaking
        if(breakTS.getHorz() == 0 && breakTS.getVert() == 0) {
            targetTile = null;
            timeAccumulator = 0;
        }else {

            Vector2 vec2 = new Vector2(breakTS.getHorz()*2, breakTS.getVert()*2).scl(0.2f);
            Vector2 tempVec = new Vector2(world.getPlayer().getCenterLocation());

            for(int i = 0; i < 6; i++) {
                if(!isInWorld(tempVec)) {
                    timeAccumulator = 0;
                    targetTile = null;
                    break;
                }
                Tile newTarget = world.getTileAt(tempVec);
                if(newTarget == null) {
                    timeAccumulator = 0;
                    targetTile = null;
                    break;
                }
                if(newTarget.isSolid()) {
                    if(newTarget != targetTile) {
                        targetTile = newTarget;
                        timeAccumulator = 0;
                    }
                    break;
                }
                tempVec.add(vec2);
            }
            if(targetTile != null) {
                timeAccumulator += Gdx.graphics.getDeltaTime();
                if(timeAccumulator >= targetTile.getTileExtraData().getDurability()) {
                    ExpiriumGame.getGame().getClientGateway().addAction(new TileBreakActionC(targetTile));
                    timeAccumulator = 0;
                    targetTile = null;
                }
            }
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

    private boolean isInWorld(Vector2 v) {
        return v.x >= 0 && v.x < world.getChunks().length * Constants.T_W_CH && v.y >= 0 && v.y < Constants.T_H_CH;
    }

    private Tile getTileAtFromScreen(int screenX, int screenY) {

        Vector3 vec =  world.getCamera().unproject(new Vector3(screenX, screenY, 0));
        int blockX = (int) (vec.x / GameWorld.PPM);
        int blockY = (int) (vec.y / GameWorld.PPM);

        int w = Constants.T_W_CH;
        int h = Constants.T_H_CH;

        if(blockX < 0 || blockX >= world.getChunks().length * w || blockY < 0 || blockY >= h) {
            return null;
        }
        int chunkID = blockX / Constants.T_W_CH;
        blockX -= chunkID * Constants.T_W_CH;
        WorldChunk chunk = world.getChunks()[chunkID];

        if(chunk == null) return null;
        Tile t = chunk.getTerrain()[blockY][blockX];
        if(t.getType() == TileType.AIR) return null;
        return t;
    }

    public void renderBreakingTile(SpriteBatch batch) {
        if(targetTile == null) return;

        float durability = timeAccumulator / targetTile.getTileExtraData().getDurability();
        int x = targetTile.getX() + Constants.T_W_CH * targetTile.getChunk().getId();
        batch.draw(Resources.TILE_BREAK_ANIM.getKeyFrame(durability), x, targetTile.getY(), 1, 1);
    }
}
