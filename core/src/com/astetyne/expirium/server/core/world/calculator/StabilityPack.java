package com.astetyne.expirium.server.core.world.calculator;

import com.astetyne.expirium.server.core.world.tile.ExpiTile;

import java.util.HashSet;
import java.util.LinkedHashSet;

public class StabilityPack {

    public HashSet<ExpiTile> strongTiles;
    public HashSet<ExpiTile> changedTiles;

    public StabilityPack() {
        strongTiles = new LinkedHashSet<>();
        changedTiles = new LinkedHashSet<>();
    }
}
