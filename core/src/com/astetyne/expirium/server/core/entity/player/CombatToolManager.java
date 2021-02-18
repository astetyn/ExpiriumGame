package com.astetyne.expirium.server.core.entity.player;

import com.astetyne.expirium.client.data.ThumbStickData;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.server.ExpiServer;
import com.astetyne.expirium.server.core.entity.ExpiEntity;
import com.astetyne.expirium.server.core.entity.LivingEntity;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class CombatToolManager extends ToolManager {

    private long lastHitTime;
    private final Vector2 tempVec, incrementDist;

    public CombatToolManager(ExpiServer server, ExpiPlayer owner) {
        super(server, owner);
        lastHitTime = 0;
        tempVec = new Vector2();
        incrementDist = new Vector2();
    }

    @Override
    public void onTick(ThumbStickData data) {

        float horz = data.horz;
        float vert = data.vert;

        if(horz == 0 && vert == 0) return;

        if(lastHitTime + 250 > System.currentTimeMillis()) return;

        incrementDist.set(horz, vert).scl(3f / Consts.COMBAT_PRECISION); // 3 tiles range

        List<ExpiEntity> copy = new ArrayList<>(server.getEntities());

        outer:
        for(ExpiEntity ee : copy) {
            if(ee == owner || !(ee instanceof LivingEntity)) continue;
            tempVec.set(owner.getCenter());

            int eX = (int) ee.getLocation().x;
            int eY = (int) ee.getLocation().y;

            for(int i = 0; i < Consts.COMBAT_PRECISION; i++) {
                if(!world.isInWorld(tempVec)) break;

                int cX = (int) tempVec.x;
                int cY = (int) tempVec.y;

                if(cX == eX && eY == cY) {
                    int damage = owner.getInv().getItemInHand().getItem().getWeaponDamage();
                    world.hitEntity(owner, (LivingEntity) ee, damage);
                    for(ExpiPlayer ep : server.getPlayers()) {
                        ep.getNetManager().putHandPunchPacket(owner);
                    }
                    lastHitTime = System.currentTimeMillis();
                    continue outer;
                }
                tempVec.add(incrementDist);
            }
        }
    }
}
