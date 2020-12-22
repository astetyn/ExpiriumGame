package com.astetyne.expirium.server.backend;

import com.astetyne.expirium.server.api.world.ExpiTile;

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
