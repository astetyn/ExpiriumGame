package com.astetyne.main.world.input;

import com.astetyne.main.gui.ThumbStick;
import com.astetyne.main.stages.GameStage;
import com.astetyne.main.utils.Constants;
import com.astetyne.main.world.GameWorld;
import com.astetyne.main.world.tiles.Tile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class TileBreaker {

    private Tile targetTile;
    private float timeAccumulator;
    private final ThumbStick breakTS;
    private final GameWorld world;
    private final SpriteBatch batch;

    public TileBreaker() {
        this.world = GameStage.get().getWorld();
        this.batch = GameStage.get().getBatch();
        breakTS = GameStage.get().getGameGUI().getBreakTS();
        targetTile = null;
        timeAccumulator = 0;
    }

    public void update() {

        // check if breaking
        if(breakTS.getHorz() == 0 && breakTS.getVert() == 0) {
            targetTile = null;
            timeAccumulator = 0;
            return;
        }

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
            /*if(timeAccumulator >= targetTile.getTileExtraData().getBreakTime()) {
                ExpiriumGame.get().getClientGateway().addSubPacket(new TileBreakPacket(targetTile));
                timeAccumulator = 0;
                targetTile = null;
            }*/
        }

    }

    private boolean isInWorld(Vector2 v) {
        return v.x >= 0 && v.x < world.getChunks().length * Constants.T_W_CH && v.y >= 0 && v.y < Constants.T_H_CH;
    }

    public void render() {

        if(targetTile == null) return;

        /*float durability = timeAccumulator / targetTile.getTileExtraData().getBreakTime();
        int x = targetTile.getX() + Constants.T_W_CH * targetTile.getChunk().getId();
        batch.draw(Resources.TILE_BREAK_ANIM.getKeyFrame(durability), x, targetTile.getY(), 1, 1);*/
    }
}
