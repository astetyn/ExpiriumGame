package com.astetyne.expirium.client.utils;

public class Consts {

    public static final boolean DEBUG = true;

    // networking
    public static final int SERVER_PORT = 1414;
    public static final int BUFFER_SIZE = 32768; //262144, 2097152, 32768
    public static final int SERVER_DEFAULT_TPS = 30;

    // world
    public static final int BREAKING_PRECISION = 6;

    public static final short DEFAULT_BIT = 1;
    public static final short PLAYER_BIT = 2;

    public static final float D_I_SIZE = 0.5f;
    public static final float D_I_PICK_DIST = 1.3f;

    public static final int DAY_TIME_SEC = 300;

    public static final float CAMPFIRE_TIME = 20;

    public static final float ITEM_COOLDOWN_BREAK = 1;
    public static final float ITEM_COOLDOWN_DROP = 6;

    public static final float ITEM_DESPAWN_TIME = 10; // in seconds

    // player
    public static final int PLAYER_INV_COLUMNS = 5;
    public static final int PLAYER_INV_ROWS = 5;
    public static final int PLAYER_INV_MAX_WEIGHT = 10;
    public static final int JUMP_DELAY = 200;

    // gui
    public static final int INV_TILE_MLT = 60;


}
