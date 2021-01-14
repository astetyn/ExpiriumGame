package com.astetyne.expirium.client.tiles;

public enum Solidity {

    /**SOLID means that it has a default-tile hitbox and it will be used as stability reference for near tiles.*/
    SOLID(false, false),
    /**SOLID_SHAPE means that it has a custom hitbox and it will be used as stability reference for near tiles.*/
    SOLID_SHAPE(false, false),
    /**SOLID_SOFT means that is does not have any hitbox and it will be used as stability reference for near tiles.*/
    SOLID_SOFT(false, true),
    /**LABILE means that it has a default-tile hitbox and it will not be used as stability reference for near tiles.*/
    LABILE(true, false),
    /**LABILE_SHAPE means that it has custom hitbox and it will not be used as stability reference for near tiles.*/
    LABILE_SHAPE(true, false),
    /**LABILE_SOFT means that it has not any hitbox and it will not be used as stability reference for near tiles.*/
    LABILE_SOFT(true, true);

    boolean labile;
    boolean soft;

    Solidity(boolean labile, boolean soft) {
        this.labile = labile;
        this.soft = soft;
    }

    public boolean isLabile() {
        return labile;
    }

    public boolean isSoft() {
        return soft;
    }
}
