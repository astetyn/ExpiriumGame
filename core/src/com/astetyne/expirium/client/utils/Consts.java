package com.astetyne.expirium.client.utils;

public class Consts {

    public static final int VERSION = 1;
    public static final String VERSION_TEXT = "alpha 1.3";

    public static final boolean DEBUG = false;

    // server
    public static final long SAVE_INTERVAL = 20000; // in millis

    // networking
    public static final int SERVER_PORT = 1414;
    public static final int BUFFER_SIZE = 131072; //262144, 2097152, 32768
    public static final int SERVER_TPS = 32;

    // world
    public static final int WORLD_BIOME_WIDTH = 70; // <-- this should NOT change
    public static final int WORLD_BIOMES_NUMBER = 20; // world width will be calculated as WORLD_BIOME_WIDTH * WORLD_BIOMES_NUMBER
    public static final int WORLD_HEIGHT = 128; // this should be used only while creating world and allow custom world size to exists

    public static final int COMBAT_PRECISION = 6;
    public static final int BREAKING_PRECISION = 6;

    public static final short DEFAULT_BIT = 1;
    public static final short PLAYER_BIT = 2;

    public static final float D_I_PICK_DIST = 1.7f;

    public static final int ITEM_COOLDOWN_BREAK = SERVER_TPS/4;
    public static final int ITEM_COOLDOWN_DROP = SERVER_TPS * 6;

    public static final int ITEM_DESPAWN_TIME = 60; // in seconds

    public static final byte MAX_LIGHT_LEVEL = 10;
    public static final float MAX_LIGHT_LEVEL_INVERTED = 1f / MAX_LIGHT_LEVEL;
    public static final int SKY_LIGHT_DECREASE = 3;

    public static final float WATER_DENSITY = 28;
    public static final byte MAX_WATER_LEVEL = 5;
    public static final int DROWNING_TICKS = SERVER_TPS * 10;
    public static final byte STARVATION_LEVEL = 5;

    public static final int TICKS_IN_HOUR = 1600;
    public static final int TICKS_IN_DAY = Consts.TICKS_IN_HOUR * 24;
    public static final int SUNRISE_START = Consts.TICKS_IN_HOUR * 6;
    public static final int SUNRISE_END = Consts.TICKS_IN_HOUR * 7;
    public static final int SUNSET_START = Consts.TICKS_IN_HOUR * 19;
    public static final int SUNSET_END = Consts.TICKS_IN_HOUR * 20;

    // player
    public static final float ACTIVE_ENTITIES_RADIUS = 30;
    public static final int PLAYER_INV_MAX_WEIGHT = 10;
    public static final int JUMP_DELAY = 200;
    public static final int INTERACT_RADIUS = 6;

    public static final float JUMP_THRESHOLD = 0.6f;
    public static final float HORZ_JUMP_THRESHOLD = 0.4f;

    public static final int WATER_MANIPULATE_COOLDOWN = 100; //in millis

    // gui
    public static final int SCREEN_WIDTH = 2000;
    public static final int SCREEN_HEIGHT = 1000;
    public static final int INV_TILE_WIDTH = 140;
    public static final int INV_MAX_SIZE = 5;

    //misc
    public static final float TPW = 32; // tiles per width of screen (how much tiles to match the screen width with default zoom)
    public static final float TPH = Utils.worldRatioFromW(TPW); // tiles per height
    public static final float MAX_ZOOM = 0.2f; // maximum zoom (max tile size)
    public static final float MIN_ZOOM = 3; // maximum un-zoom (max tiles on screen)

}
