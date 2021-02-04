package com.astetyne.expirium.client.utils;

public class Consts {

    public static final int VERSION = 1;

    public static final boolean DEBUG = false;

    // server
    public static final long SAVE_INTERVAL = 10000; // in millis

    // networking
    public static final int SERVER_PORT = 1414;
    public static final int BUFFER_SIZE = 32768; //262144, 2097152, 32768
    public static final int SERVER_DEFAULT_TPS = 30;
    public static final String MULTICAST_ADDRESS = "234.14.14.14";

    // world
    public static final int BREAKING_PRECISION = 6;

    public static final short DEFAULT_BIT = 1;
    public static final short PLAYER_BIT = 2;

    public static final float D_I_PICK_DIST = 1.3f;

    public static final float CAMPFIRE_TIME = 180;

    public static final float ITEM_COOLDOWN_BREAK = 1;
    public static final float ITEM_COOLDOWN_DROP = 6;

    public static final float ITEM_DESPAWN_TIME = 60; // in seconds

    public static final byte MAX_LIGHT_LEVEL = 10;
    public static final int SKY_LIGHT_DECREASE = 3;

    public static final int DAY_TIME_SEC = 1200;
    public static final int SUNRISE_START = 0;
    public static final int SUNRISE_END = 50;
    public static final int SUNSET_START = 800;
    public static final int SUNSET_END = 850;

    // player
    public static final int PLAYER_INV_COLUMNS = 5;
    public static final int PLAYER_INV_ROWS = 5;
    public static final int PLAYER_INV_MAX_WEIGHT = 10;
    public static final int JUMP_DELAY = 200;
    public static final int INTERACT_RADIUS = 6;

    // gui
    public static final int INV_TILE_MLT = 120;


}
