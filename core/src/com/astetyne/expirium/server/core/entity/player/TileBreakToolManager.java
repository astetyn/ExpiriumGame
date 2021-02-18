package com.astetyne.expirium.server.core.entity.player;

import com.astetyne.expirium.client.data.ThumbStickData;
import com.astetyne.expirium.client.tiles.Material;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.event.Source;
import com.astetyne.expirium.server.core.world.tile.ExpiTile;
import com.badlogic.gdx.math.Vector2;

public class TileBreakToolManager extends ToolManager {

    private ExpiTile targetTile;
    private float timeAccumulator;
    private final Vector2 tempVec, incrementDist;
    private long lastPunchTime;

    public TileBreakToolManager(ExpiServer server, ExpiPlayer owner) {
        super(server, owner);
        targetTile = null;
        timeAccumulator = 0;
        tempVec = new Vector2();
        incrementDist = new Vector2();
        lastPunchTime = 0;
    }

    @Override
    public void onTick(ThumbStickData data) {

        float horz = data.horz;
        float vert = data.vert;

        if(horz == 0 && vert == 0) {
            cancelBreaking();
            return;
        }

        tempVec.set(owner.getCenter());
        incrementDist.set(horz, vert).scl(2f / Consts.BREAKING_PRECISION);

        boolean tileFound = false;

        for(int i = 0; i < Consts.BREAKING_PRECISION; i++) {
            if(!world.isInWorld(tempVec)) {
                cancelBreaking();
                break;
            }
            ExpiTile checkTile = world.getTileAt(tempVec);

            if(checkTile.getMaterial() == Material.AIR || checkTile.getY() == 0) {
                tempVec.add(incrementDist);
                continue;
            }

            if(checkTile != targetTile) {
                timeAccumulator = 0;
                targetTile = checkTile;
            }

            tileFound = true;
            break;
        }

        if(!tileFound) {
            cancelBreaking();
            return;
        }

        float speedCoef = owner.getInv().getItemInHand().getItem().getBreakingSpeedCoef();
        if(Consts.DEBUG) speedCoef = 50;
        timeAccumulator += speedCoef / Consts.SERVER_TPS;
        if(timeAccumulator >= targetTile.getMaterial().getBreakTime()) {
            world.changeMaterial(targetTile, Material.AIR, true, Source.PLAYER);
            cancelBreaking();
        }else {
            // breaking in action
            for(ExpiPlayer ep : server.getPlayers()) {
                ep.getNetManager().putBreakingTilePacket(targetTile, timeAccumulator / targetTile.getMaterial().getBreakTime());
            }
            if(lastPunchTime + 200 < System.currentTimeMillis()) { // cca 450 is full animation (200 is half)
                lastPunchTime = System.currentTimeMillis();
                for(ExpiPlayer ep : server.getPlayers()) {
                    ep.getNetManager().putHandPunchPacket(owner);
                }
            }
        }
    }

    @Override
    public void end() {
        cancelBreaking();
    }

    private void cancelBreaking() {
        targetTile = null;
        timeAccumulator = 0;
    }

}
