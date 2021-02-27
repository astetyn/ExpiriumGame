package com.astetyne.expirium.server.core.entity.player;

import com.astetyne.expirium.client.data.ThumbStickData;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.world.input.InteractType;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.event.Source;
import com.astetyne.expirium.server.core.world.tile.Material;
import com.astetyne.expirium.server.core.world.tile.Tile;
import com.badlogic.gdx.math.Vector2;

public class TileBreakToolManager extends ToolManager {

    private Tile targetTile;
    private float timeAccumulator;
    private final Vector2 tempVec, incrementDist;
    private long lastPunchTime;
    private long clickTime;

    public TileBreakToolManager(ExpiServer server, Player owner, ThumbStickData data) {
        super(server, owner, data);
        targetTile = null;
        timeAccumulator = 0;
        tempVec = new Vector2();
        incrementDist = new Vector2();
        lastPunchTime = 0;
        clickTime = -1;
    }

    public void onInteract(Tile t, InteractType type) {

        if(type == InteractType.PRESS) {
            if(!data.isNeutral()) return;
            clickTime = System.currentTimeMillis();
            targetTile = t;
        }else if(type == InteractType.RELEASE) {
            clickTime = -1;
        }else if(type == InteractType.DRAG && t != targetTile) {
            if(!data.isNeutral()) return;
            clickTime = System.currentTimeMillis();
            targetTile = t;
            timeAccumulator = 0;
        }

    }

    @Override
    public void onTick() {

        if(!findTile()) {

            if(clickTime != -1) {
                if(clickTime + 500 > System.currentTimeMillis()) return;
            }else {
                cancelBreaking();
                return;
            }
        }

        float speedCoef = owner.getInv().getItemInHand().getItem().getBreakingSpeedCoef();
        if(Consts.DEBUG) speedCoef = 50;
        timeAccumulator += speedCoef / Consts.SERVER_TPS;
        if(timeAccumulator >= targetTile.getMaterial().getBreakTime()) {
            world.changeMaterial(targetTile, Material.AIR, true, Source.PLAYER);
            cancelBreaking();
        }else {
            // breaking in action
            for(Player ep : server.getPlayers()) {
                ep.getNetManager().putBreakingTilePacket(targetTile, timeAccumulator / targetTile.getMaterial().getBreakTime());
            }
            if(lastPunchTime + 200 < System.currentTimeMillis()) { // cca 450 is full animation (200 is half)
                lastPunchTime = System.currentTimeMillis();
                for(Player ep : server.getPlayers()) {
                    ep.getNetManager().putHandPunchPacket(owner);
                }
            }
        }
    }

    private boolean findTile() {

        float horz = data.horz;
        float vert = data.vert;

        if(horz == 0 && vert == 0) {
            return false;
        }

        tempVec.set(owner.getCenter());
        incrementDist.set(horz, vert).scl(3f / Consts.BREAKING_PRECISION);

        for(int i = 0; i < Consts.BREAKING_PRECISION; i++) {
            if(!world.isInWorld(tempVec)) {
                break;
            }
            Tile checkTile = world.getTileAt(tempVec);

            if(checkTile.getMaterial() == Material.AIR || checkTile.getY() == 0) {
                tempVec.add(incrementDist);
                continue;
            }
            if(checkTile != targetTile) {
                timeAccumulator = 0;
                targetTile = checkTile;
            }
            return true;
        }
        return false;
    }

    @Override
    public void end() {
        cancelBreaking();
    }

    private void cancelBreaking() {
        targetTile = null;
        timeAccumulator = 0;
        clickTime = -1;
    }

}
