package com.astetyne.expirium.server.api.event;

import java.util.ArrayList;
import java.util.List;

public class EventManager {

    private final List<TickListener> tickListeners;
    private final List<PlayerInteractListener> playerInteractListeners;
    private final List<TileChangeListener> tileChangeListeners;

    public EventManager() {
        tickListeners = new ArrayList<>();
        playerInteractListeners = new ArrayList<>();
        tileChangeListeners = new ArrayList<>();
    }

    public List<TickListener> getTickListeners() {
        return tickListeners;
    }

    public List<PlayerInteractListener> getPlayerInteractListeners() {
        return playerInteractListeners;
    }

    public List<TileChangeListener> getTileChangeListeners() {
        return tileChangeListeners;
    }
}
