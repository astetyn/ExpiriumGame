package com.astetyne.expirium.server.core.entity;

import com.astetyne.expirium.client.data.ThumbStickData;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.tiles.TileType;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.event.Source;
import com.astetyne.expirium.server.core.world.ExpiWorld;
import com.astetyne.expirium.server.core.world.tile.ExpiTile;
import com.badlogic.gdx.math.Vector2;

public class ExpiTileBreaker {

    private final ExpiServer server;
    private ExpiTile targetTile;
    private float timeAccumulator;
    private final Vector2 tempVec, tempVec2;
    private final ExpiWorld world;
    private final ExpiPlayer owner;
    private long lastPunchTime;

    public ExpiTileBreaker(ExpiServer server, ExpiPlayer owner) {
        this.server = server;
        this.owner = owner;
        targetTile = null;
        timeAccumulator = 0;
        tempVec = new Vector2();
        tempVec2 = new Vector2();
        world = server.getWorld();
        lastPunchTime = 0;
    }

    public void onTick(ThumbStickData data) {

        float horz = data.horz;
        float vert = data.vert;

        // check if breaking
        if(horz == 0 && vert == 0) {
            targetTile = null;
            timeAccumulator = 0;
            return;
        }

        tempVec.set(owner.getCenter());
        tempVec2.set(horz*2, vert*2).scl(1.0f/(Consts.BREAKING_PRECISION-1));

        for(int i = 0; i < Consts.BREAKING_PRECISION; i++) {
            if(!isInWorld(tempVec)) {
                timeAccumulator = 0;
                targetTile = null;
                break;
            }
            ExpiTile newTarget = world.getTileAt(tempVec);
            if(newTarget == null) {
                timeAccumulator = 0;
                targetTile = null;
                break;
            }
            if(newTarget.getType() != TileType.AIR) {
                if(newTarget != targetTile && newTarget.getY() != 0) {
                    timeAccumulator = 0;
                    targetTile = newTarget;
                }
                break;
            }
            tempVec.add(tempVec2);
        }
        if(targetTile != null) {
            float speedCoef = 1;
            if(owner.getInv().getItemInHand().getItem() == Item.WOODEN_MATTOCK) {
                speedCoef = 2;
            }else if(owner.getInv().getItemInHand().getItem() == Item.RHYOLITE_MATTOCK) {
                speedCoef = 3;
            }
            if(Consts.DEBUG) speedCoef = 50;
            timeAccumulator += speedCoef / Consts.SERVER_TPS;
            if(timeAccumulator >= targetTile.getType().getBreakTime()) {
                world.changeTile(targetTile, TileType.AIR, true, owner, Source.PLAYER);
                timeAccumulator = 0;
                targetTile = null;
            }else {
                // breaking in action
                for(ExpiPlayer ep : server.getPlayers()) {
                    ep.getNetManager().putBreakingTilePacket(targetTile, timeAccumulator / targetTile.getType().getBreakTime());
                }
                if(lastPunchTime + 200 < System.currentTimeMillis()) { // cca 450 is full animation (200 is half)
                    lastPunchTime = System.currentTimeMillis();
                    for(ExpiPlayer ep : server.getPlayers()) {
                        ep.getNetManager().putHandPunchPacket(owner);
                    }
                }
            }
        }

    }

    private boolean isInWorld(Vector2 v) {
        return v.x >= 0 && v.x < world.getTerrainWidth() && v.y >= 0 && v.y < world.getTerrainHeight();
    }

}
