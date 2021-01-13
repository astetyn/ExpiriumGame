package com.astetyne.expirium.main.world.tiles;

import com.astetyne.expirium.main.utils.IntVector2;

import java.util.HashMap;

public class BreakingTile {

    private final static HashMap<Tile, BreakingTile> breakingTiles = new HashMap<>();

    private final IntVector2 loc;
    private float state;

    public BreakingTile(IntVector2 loc, float state) {
        this.loc = loc;
        this.state = state;
    }

    public IntVector2 getLoc() {
        return loc;
    }

    public float getState() {
        return state;
    }

    public void setState(float state) {
        this.state = state;
    }

    public static HashMap<Tile, BreakingTile> getBreakingTiles() {
        return breakingTiles;
    }
}
