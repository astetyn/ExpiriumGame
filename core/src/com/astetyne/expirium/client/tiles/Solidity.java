package com.astetyne.expirium.client.tiles;

public enum Solidity {

    /**SOLID means that it will be used as stability reference for near tiles.*/
    SOLID(false, false),
    /**SOLID_VERT means that it will be used as stability reference for near tiles
     * and will only check stability reference from tile under it.*/
    SOLID_VERT(false, true),
    /**LABILE means that it will not be used as stability reference for near tiles.*/
    LABILE(true, false),
    /**LABILE means that it will not be used as stability reference for near tiles
     * and will only check stability reference from tile under it.*/
    LABILE_VERT(true, true),
    /**ONLY_VERT means that it will be used as stability reference only for tile above it and will only check stability
     * reference from tile under it. This is useful for supports or logs.*/
    ONLY_VERT(true, true);

    boolean labile;
    boolean vert;

    Solidity(boolean labile, boolean vert) {
        this.labile = labile;
        this.vert = vert;
    }

    public boolean isLabile() {
        return labile;
    }

    public boolean isVert() {
        return vert;
    }
}
