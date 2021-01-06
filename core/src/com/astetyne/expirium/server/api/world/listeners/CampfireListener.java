package com.astetyne.expirium.server.api.world.listeners;

import com.astetyne.expirium.main.utils.Consts;
import com.astetyne.expirium.main.world.tiles.TileType;
import com.astetyne.expirium.server.GameServer;
import com.astetyne.expirium.server.api.world.ExpiTile;
import com.astetyne.expirium.server.api.world.TileListener;

import java.util.*;

public class CampfireListener implements TileListener {

    private final List<ExpiTile> activeCampfires;
    private final HashMap<ExpiTile, Float> timeTillEnd;

    public CampfireListener() {
        activeCampfires = new ArrayList<>();
        timeTillEnd = new HashMap<>();
    }

    @Override
    public void onTick() {
        Iterator<Map.Entry<ExpiTile, Float>> it = timeTillEnd.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<ExpiTile, Float> entry = it.next();
            float time = entry.getValue();
            time -= 1.0f / Consts.SERVER_DEFAULT_TPS;
            if(time <= 0) {
                GameServer.get().getWorld().changeTile(entry.getKey(), TileType.AIR, false);
                activeCampfires.remove(entry.getKey());
                it.remove();
            }else if(time < 5) {
                entry.getKey().setType(TileType.CAMPFIRE_SMALL);
            }
            entry.setValue(time);
        }
    }

    @Override
    public void onTileChange(ExpiTile t) {
        if(t.getType() == TileType.CAMPFIRE_BIG || t.getType() == TileType.CAMPFIRE_SMALL) {
            activeCampfires.remove(t);
            timeTillEnd.remove(t);
            System.out.println("break");
        }
    }

    @Override
    public void onTilePlace(ExpiTile t) {
        if(t.getType() == TileType.CAMPFIRE_BIG) {
            activeCampfires.add(t);
            timeTillEnd.put(t, Consts.CAMPFIRE_TIME);
            System.out.println("place");
        }
    }

    @Override
    public void onTileInteract(ExpiTile t) {

    }
}
