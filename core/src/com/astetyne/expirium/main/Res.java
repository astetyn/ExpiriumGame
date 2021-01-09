package com.astetyne.expirium.main;

import com.astetyne.expirium.main.gui.widget.BaseGrid;
import com.astetyne.expirium.main.gui.widget.HotBarSlot;
import com.astetyne.expirium.main.gui.widget.SwitchArrow;
import com.astetyne.expirium.main.gui.widget.ThumbStick;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Res {

    // gui
    public static BitmapFont MAIN_FONT;

    public static Button.ButtonStyle BUTTON_STYLE;
    public static TextureRegion CROSS_ICON;
    public static TextButton.TextButtonStyle TEXT_BUTTON_STYLE;
    public static TextField.TextFieldStyle TEXT_FIELD_STYLE;
    public static Label.LabelStyle LABEL_STYLE;
    public static ThumbStick.ThumbStickStyle THUMB_STICK_STYLE;
    public static HotBarSlot.HotBarSlotStyle HOT_BAR_SLOT_STYLE;
    public static SwitchArrow.SwitchArrowStyle SWITCH_ARROW_STYLE;
    public static BaseGrid.BaseGridStyle BASE_GRID_STYLE;

    public static TextureRegion HEALTH_ICON, FOOD_ICON, TEMP_ICON;

    // gui - inv
    public static TextureRegion INVENTORY;
    public static TextureRegion INV_CHOOSE_BACK, INV_DETAIL_BACK;
    public static TextureRegion INV_WEIGHT;
    public static Drawable RECIPE_BACK;

    // gui - items
    public static TextureRegion RHYOLITE_PICKAXE_ITEM;
    public static TextureRegion GRASS_ITEM;
    public static TextureRegion DIRT_ITEM;
    public static TextureRegion STONE_ITEM;
    public static TextureRegion RAW_WOOD_ITEM;
    public static TextureRegion WOODEN_WALL_ITEM;
    public static TextureRegion CAMPFIRE_ITEM;

    // gui - items - grid

    // entities
    public static Animation<TextureRegion> PLAYER_IDLE_ANIM_R;
    public static Animation<TextureRegion> PLAYER_IDLE_ANIM_L;
    public static Animation<TextureRegion> PLAYER_RUN_ANIM_R;
    public static Animation<TextureRegion> PLAYER_RUN_ANIM_L;

    // world
    public static TextureRegion LIGHT_SPH_1;

    public static TextureRegion STONE_TILE;
    public static TextureRegion GRASS_TILE;
    public static TextureRegion DIRT_TILE;
    public static TextureRegion TREE1_TILE, TREE2_TILE, TREE3_TILE, TREE4_TILE, TREE5_TILE, TREE6_TILE;
    public static TextureRegion WHITE_TILE;
    public static TextureRegion WOODEN_WALL_TILE;

    public static Animation<TextureRegion> CAMPFIRE_SMALL_TILE, CAMPFIRE_BIG_TILE;

    public static Animation<TextureRegion> TILE_BREAK_ANIM;

    // background
    public static TextureRegion BG_1, BG_2, BG_3;
    public static TextureRegion SUN, MOON;

    public static void loadTextures() {

        Animation.PlayMode loop = Animation.PlayMode.LOOP;
        Animation.PlayMode loop_pong = Animation.PlayMode.LOOP_PINGPONG;

        TextureAtlas world = new TextureAtlas("world.atlas");
        TextureAtlas gui = new TextureAtlas("gui.atlas");
        TextureAtlas ent = new TextureAtlas("entities.atlas");
        TextureAtlas bg = new TextureAtlas("background.atlas");

        // gui
        MAIN_FONT = new BitmapFont(Gdx.files.internal("arial_medium.fnt"), gui.findRegion("arial_medium"));
        MAIN_FONT.getData().setScale((float)Gdx.graphics.getHeight() / Gdx.graphics.getWidth(), 1);

        Drawable up = new TextureRegionDrawable(gui.findRegion("button_base"));
        Drawable down = new TextureRegionDrawable(gui.findRegion("button_pressed"));
        Drawable selection = new TextureRegionDrawable(gui.findRegion("selection"));
        Drawable cursor = new TextureRegionDrawable(gui.findRegion("cursor"));
        TextureRegion tsBack = gui.findRegion("thumb_stick_back");
        TextureRegion tsFore = gui.findRegion("thumb_stick_fore");
        TextureRegion hbsFrame = gui.findRegion("hot_bar_slot_frame");
        TextureRegion switchArrowUp = gui.findRegion("switch_arrow_up");
        TextureRegion switchArrowUpPressed = gui.findRegion("switch_arrow_up_pressed");
        TextureRegion invTile = gui.findRegion("inv_tile");
        TextureRegion invTileSplit = gui.findRegion("inv_tile_split");
        TextureRegion invTileThrow = gui.findRegion("inv_tile_throw");

        CROSS_ICON = gui.findRegion("cross_icon");

        BUTTON_STYLE = new Button.ButtonStyle(up, down, up);
        TEXT_BUTTON_STYLE = new TextButton.TextButtonStyle(up, down, up, MAIN_FONT);
        TEXT_FIELD_STYLE = new TextField.TextFieldStyle(MAIN_FONT, Color.WHITE, cursor, selection, up);
        LABEL_STYLE = new Label.LabelStyle(MAIN_FONT, Color.WHITE);
        THUMB_STICK_STYLE = new ThumbStick.ThumbStickStyle(tsBack, tsFore);
        HOT_BAR_SLOT_STYLE = new HotBarSlot.HotBarSlotStyle(hbsFrame);
        SWITCH_ARROW_STYLE = new SwitchArrow.SwitchArrowStyle(switchArrowUp, switchArrowUpPressed);
        BASE_GRID_STYLE = new BaseGrid.BaseGridStyle(invTile, invTileThrow, invTileSplit);

        HEALTH_ICON = gui.findRegion("health_icon");
        FOOD_ICON = gui.findRegion("food_icon");
        TEMP_ICON = gui.findRegion("temperature_icon");

        // gui - inv
        INVENTORY = gui.findRegion("inventory");
        RECIPE_BACK = new TextureRegionDrawable(gui.findRegion("recipe_list_background"));
        INV_DETAIL_BACK = gui.findRegion("item_detail_back");
        INV_CHOOSE_BACK = gui.findRegion("item_choose_back");
        INV_WEIGHT = gui.findRegion("weight_icon");

        // gui - items
        RHYOLITE_PICKAXE_ITEM = gui.findRegion("rhyolite_pickaxe_item");
        GRASS_ITEM = gui.findRegion("grass_item");
        DIRT_ITEM = gui.findRegion("dirt_item");
        STONE_ITEM = gui.findRegion("stone_item");
        RAW_WOOD_ITEM = gui.findRegion("raw_wood_item");
        WOODEN_WALL_ITEM = gui.findRegion("wooden_wall_item");
        CAMPFIRE_ITEM = gui.findRegion("campfire_item");

        // gui - items - grid

        // entities
        PLAYER_IDLE_ANIM_R = new Animation<>(0.5f, ent.findRegions("player_right_idle"), loop_pong);
        PLAYER_IDLE_ANIM_L = new Animation<>(0.5f, ent.findRegions("player_left_idle"), loop_pong);
        PLAYER_RUN_ANIM_R = new Animation<>(0.1f, ent.findRegions("player_right_run"), loop_pong);
        PLAYER_RUN_ANIM_L = new Animation<>(0.1f, ent.findRegions("player_left_run"), loop_pong);

        // world
        LIGHT_SPH_1 = new TextureRegion(new Texture(Gdx.files.internal("lightning.png")));

        STONE_TILE = world.findRegion("stone_tile");
        GRASS_TILE = world.findRegion("grass");
        DIRT_TILE = world.findRegion("dirt_tile");
        TREE1_TILE = world.findRegion("tree1");
        TREE2_TILE = world.findRegion("tree2");
        TREE3_TILE = world.findRegion("tree3");
        TREE4_TILE = world.findRegion("tree4");
        TREE5_TILE = world.findRegion("tree5");
        TREE6_TILE = world.findRegion("tree6");
        WHITE_TILE = world.findRegion("white_tile");
        WOODEN_WALL_TILE = world.findRegion("wooden_wall");

        CAMPFIRE_SMALL_TILE = new Animation<>(0.1f, world.findRegions("campfire_small"), loop_pong);
        CAMPFIRE_BIG_TILE = new Animation<>(0.1f, world.findRegions("campfire_big"), loop_pong);

        TILE_BREAK_ANIM = new Animation<>(0.26f, world.findRegions("tile_break"), loop);

        // background
        BG_1 = bg.findRegion("bg1"); // back
        BG_2 = bg.findRegion("bg2"); // middle
        BG_3 = bg.findRegion("bg3"); // front
        SUN = bg.findRegion("sun");
        MOON = bg.findRegion("moon");

    }

    public static void dispose() {
        MAIN_FONT.dispose();
    }

}
