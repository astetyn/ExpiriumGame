package com.astetyne.expirium.server.backend;

import com.astetyne.expirium.main.items.Item;
import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.main.world.tiles.TileType;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.entities.ExpiPlayer;
import com.astetyne.expirium.server.api.world.ExpiTile;
import com.astetyne.expirium.server.api.world.ExpiWorld;
import com.badlogic.gdx.math.Vector2;

public class ExpiTileBreaker {

    private ExpiTile targetTile;
    private float timeAccumulator;
    private final Vector2 tempVec, tempVec2;
    private final ExpiWorld world;
    private final ExpiPlayer owner;

    public ExpiTileBreaker(ExpiPlayer owner) {
        this.owner = owner;
        targetTile = null;
        timeAccumulator = 0;
        tempVec = new Vector2();
        tempVec2 = new Vector2();
        world = GameServer.get().getWorld();
    }

    public void update(float horz, float vert) {

        // check if breaking
        if(horz == 0 && vert == 0) {
            if(targetTile != null) owner.getGateway().getManager().putBreakingTile(targetTile, -1);
            targetTile = null;
            timeAccumulator = 0;
            return;
        }

        tempVec.set(owner.getCenter());
        tempVec2.set(horz*2, vert*2).scl(1.0f/(Consts.BREAKING_PRECISION-1));

        for(int i = 0; i < Consts.BREAKING_PRECISION; i++) {
            if(!isInWorld(tempVec)) {
                timeAccumulator = 0;
                if(targetTile != null) owner.getGateway().getManager().putBreakingTile(targetTile, -1);
                targetTile = null;
                break;
            }
            ExpiTile newTarget = world.getTileAt(tempVec);
            if(newTarget == null) {
                timeAccumulator = 0;
                if(targetTile != null) owner.getGateway().getManager().putBreakingTile(targetTile, -1);
                targetTile = null;
                break;
            }
            if(newTarget.getType() != TileType.AIR) {
                if(newTarget != targetTile) {
                    timeAccumulator = 0;
                    if(targetTile != null) owner.getGateway().getManager().putBreakingTile(targetTile, -1);
                    targetTile = newTarget;
                }
                break;
            }
            tempVec.add(tempVec2);
        }
        if(targetTile != null) {
            float speedCoef = 1;
            if(owner.getInv().getItemInHand().getItem() != Item.EMPTY) {
                speedCoef = owner.getInv().getItemInHand().getItem().getSpeedCoef();
            }
            if(Consts.DEBUG) speedCoef = 50;
            timeAccumulator += speedCoef / Consts.SERVER_DEFAULT_TPS;
            if(timeAccumulator >= targetTile.getType().getBreakTime()) {
                GameServer.get().getWorld().changeTile(targetTile, TileType.AIR, true);
                timeAccumulator = 0;
                owner.getGateway().getManager().putBreakingTile(targetTile, -1);
                targetTile = null;
            }else {
                owner.getGateway().getManager().putBreakingTile(targetTile, timeAccumulator);
            }
        }

    }

    private boolean isInWorld(Vector2 v) {
        return v.x >= 0 && v.x < world.getTerrainWidth() && v.y >= 0 && v.y < world.getTerrainHeight();
    }

}
