package com.astetyne.expirium.client.tiles;

public enum Solidity {

    /**SOLID means that it has a default-tile hitbox and it will be used as stability reference for near tiles.*/
    SOLID(false, false, false),
    /**SOLID_SOFT means that is does not have any hitbox and it will be used as stability reference for near tiles.*/
    SOLID_SOFT(false, true, false),
    /**SOLID_SOFT_VERT means that is does not have any hitbox, it will be used as stability reference for near tiles
     * and will only check stability reference from tile under it.*/
    SOLID_SOFT_VERT(false, true, true),
    /**LABILE means that it has a default-tile hitbox and it will not be used as stability reference for near tiles.*/
    LABILE(true, false, false),
    /**LABILE_SOFT means that it has not any hitbox and it will not be used as stability reference for near tiles.*/
    LABILE_SOFT(true, true, false),
    /**LABILE means that it has a default-tile hitbox, it will not be used as stability reference for near tiles
     * and will only check stability reference from tile under it.*/
    LABILE_VERT(true, true, true),
    /**LABILE_SOFT_VERT means that it has not any hitbox, it will not be used as stability reference for near tiles
     * and will only check stability reference from tile under it.*/
    LABILE_SOFT_VERT(true, true, true);

    boolean labile;
    boolean soft;
    boolean vert;

    Solidity(boolean labile, boolean soft, boolean vert) {
        this.labile = labile;
        this.soft = soft;
        this.vert = vert;
    }

    public boolean isLabile() {
        return labile;
    }

    public boolean isSoft() {
        return soft;
    }

    public boolean isVert() {
        return vert;
    }
}
