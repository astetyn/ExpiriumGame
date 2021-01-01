package com.astetyne.expirium.server.api.world.listeners;

import com.astetyne.expirium.main.utils.Constants;
import com.astetyne.expirium.main.world.tiles.TileType;
import com.astetyne.expirium.server.api.world.ExpiTile;
import com.astetyne.expirium.server.api.world.TileListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CampfireListener implements TileListener {

    private final List<ExpiTile> activeCampfires;
    private final HashMap<ExpiTile, Float> timeTillEnd;

    public CampfireListener() {
        activeCampfires = new ArrayList<>();
        timeTillEnd = new HashMap<>();
    }

    @Override
    public void onTick() {
        for(Map.Entry<ExpiTile, Float> entry : timeTillEnd.entrySet()) {
            float time = entry.getValue();
            time -= 1.0f / Constants.SERVER_DEFAULT_TPS;
            if(time <= 0) {
                //todo:
            }else if(time < 20) {
                entry.getKey().setType(TileType.CAMPFIRE_SMALL);
            }
            entry.setValue(time);
        }
    }

    @Override
    public void onTilePreBreak(ExpiTile t) {
        if(t.getType() == TileType.CAMPFIRE_BIG || t.getType() == TileType.CAMPFIRE_SMALL) {
            activeCampfires.remove(t);
            timeTillEnd.remove(t);
        }
    }

    @Override
    public void onTilePlace(ExpiTile t) {
        if(t.getType() == TileType.CAMPFIRE_BIG) {
            activeCampfires.add(t);
            timeTillEnd.put(t, Constants.CAMPFIRE_TIME);
        }
    }

    @Override
    public void onTileInteract(ExpiTile t) {

    }
}
