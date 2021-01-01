package com.astetyne.expirium.main.world.tiles;

public enum Solidity {

    /**SOLID means that it has a default-tile hitbox and it will be used as stability reference for near tiles.*/
    SOLID(false),
    /**SOLID_SHAPE means that it has a custom hitbox and it will be used as stability reference for near tiles.*/
    SOLID_SHAPE(false),
    /**SOLID_SOFT means that is does not have any hitbox and it will be used as stability reference for near tiles.*/
    SOLID_SOFT(false),
    /**LABILE means that it has a default-tile hitbox and it will not be used as stability reference for near tiles.*/
    LABILE(true),
    /**LABILE_SHAPE means that it has custom hitbox and it will not be used as stability reference for near tiles.*/
    LABILE_SHAPE(true),
    /**LABILE_SOFT means that it has not any hitbox and it will not be used as stability reference for near tiles.*/
    LABILE_SOFT(true);

    boolean labile;

    Solidity(boolean labile) {
        this.labile = labile;
    }

    public boolean isLabile() {
        return labile;
    }
}
