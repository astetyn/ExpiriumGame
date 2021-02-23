package com.astetyne.expirium.server.core.world.calculator;

import com.astetyne.expirium.server.core.world.tile.Tile;

import java.util.HashSet;
import java.util.LinkedHashSet;

public class StabilityPack {

    public HashSet<Tile> strongTiles;
    public HashSet<Tile> changedTiles;

    public StabilityPack() {
        strongTiles = new LinkedHashSet<>();
        changedTiles = new LinkedHashSet<>();
    }
}
