package com.astetyne.expirium.main.world.input;

import com.astetyne.expirium.main.ExpiriumGame;
import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.gui.ThumbStick;
import com.astetyne.expirium.main.stages.GameStage;
import com.astetyne.expirium.main.utils.Constants;
import com.astetyne.expirium.main.world.GameWorld;
import com.astetyne.expirium.main.world.tiles.Tile;
import com.astetyne.expirium.main.world.tiles.TileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class TileBreaker {

    private Tile targetTile;
    private float timeAccumulator;
    private final ThumbStick breakTS;
    private final GameWorld world;
    private final SpriteBatch batch;
    private final Vector2 tempVec;
    private final Vector2 tempVec2;

    public TileBreaker() {
        this.world = GameStage.get().getWorld();
        this.batch = GameStage.get().getBatch();
        breakTS = new ThumbStick(Res.THUMB_STICK_STYLE);
        targetTile = null;
        timeAccumulator = 0;
        tempVec = new Vector2();
        tempVec2 = new Vector2();
    }

    public void update() {

        // check if breaking
        if(breakTS.getHorz() == 0 && breakTS.getVert() == 0) {
            targetTile = null;
            timeAccumulator = 0;
            return;
        }

        tempVec.set(world.getPlayer().getCenterLocation());
        tempVec2.set(breakTS.getHorz()*2, breakTS.getVert()*2).scl(1.0f/(Constants.BREAKING_PRECISION-1));

        for(int i = 0; i < Constants.BREAKING_PRECISION; i++) {
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
            if(newTarget.getType() != TileType.AIR) {
                if(newTarget != targetTile) {
                    targetTile = newTarget;
                    timeAccumulator = 0;
                }
                break;
            }
            tempVec.add(tempVec2);
        }
        if(targetTile != null) {
            float speedCoef = 1;
            if(GameStage.get().getInv().getItemInHand() != null) {
                speedCoef = GameStage.get().getInv().getItemInHand().getItem().getSpeedCoef();
            }
            if(Constants.DEBUG) speedCoef = 50;
            timeAccumulator += Gdx.graphics.getDeltaTime() * speedCoef;
            if(timeAccumulator >= targetTile.getType().getBreakTime()) {
                ExpiriumGame.get().getClientGateway().getManager().putTileBreakReqPacket(targetTile);
                timeAccumulator = 0;
                targetTile = null;
            }
        }

    }

    private boolean isInWorld(Vector2 v) {
        return v.x >= 0 && v.x < world.getChunks().length * Constants.T_W_CH && v.y >= 0 && v.y < Constants.T_H_CH;
    }

    public void render() {

        if(targetTile == null) return;

        float durability = timeAccumulator / targetTile.getType().getBreakTime();
        int x = targetTile.getX() + Constants.T_W_CH * targetTile.getC();
        batch.draw(Res.TILE_BREAK_ANIM.getKeyFrame(durability), x, targetTile.getY(), 1, 1);
    }

    public ThumbStick getBreakTS() {
        return breakTS;
    }
}
