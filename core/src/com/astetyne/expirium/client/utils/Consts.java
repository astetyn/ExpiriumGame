package com.astetyne.expirium.client.utils;

public class Consts {

    public static final int VERSION = 1;
    public static final String VERSION_TEXT = "alpha 1.2";

    public static final boolean DEBUG = true;

    // server
    public static final long SAVE_INTERVAL = 20000; // in millis

    // networking
    public static final int SERVER_PORT = 1414;
    public static final int BUFFER_SIZE = 131072; //262144, 2097152, 32768
    public static final int SERVER_TPS = 32;
    public static final String MULTICAST_ADDRESS = "234.14.14.14";

    // world
    public static final int COMBAT_PRECISION = 6;

    public static final int BREAKING_PRECISION = 6;

    public static final short DEFAULT_BIT = 1;
    public static final short PLAYER_BIT = 2;

    public static final float D_I_PICK_DIST = 1.7f;

    public static final float CAMPFIRE_TIME = 180;

    public static final int BIOME_LEN = 100;

    public static final int ITEM_COOLDOWN_BREAK = SERVER_TPS/4;
    public static final int ITEM_COOLDOWN_DROP = SERVER_TPS * 6;

    public static final int ITEM_DESPAWN_TIME = 60; // in seconds

    public static final byte MAX_LIGHT_LEVEL = 10;
    public static final float MAX_LIGHT_LEVEL_INVERTED = 1f / MAX_LIGHT_LEVEL;
    public static final int SKY_LIGHT_DECREASE = 3;

    public static final byte MAX_WATER_LEVEL = 5;

    public static final int TICKS_IN_DAY = 38400;
    public static final int TICKS_IN_HOUR = 1600;
    public static final int SUNRISE_START = 9600;
    public static final int SUNRISE_END = 11200;
    public static final int SUNSET_START = 30400;
    public static final int SUNSET_END = 32000;

    // player
    public static final float ACTIVE_ENTITIES_RADIUS = 30;
    public static final int PLAYER_INV_COLUMNS = 5;
    public static final int PLAYER_INV_ROWS = 5;
    public static final int PLAYER_INV_MAX_WEIGHT = 10;
    public static final int JUMP_DELAY = 200;
    public static final int INTERACT_RADIUS = 6;

    public static final float JUMP_THRESHOLD = 0.6f;
    public static final float HORZ_JUMP_THRESHOLD = 0.4f;

    // gui
    public static final int INV_TILE_MLT = 140;

}
