package com.astetyne.expirium.main;

import com.astetyne.expirium.main.gui.HotBarSlot;
import com.astetyne.expirium.main.gui.SwitchArrow;
import com.astetyne.expirium.main.gui.ThumbStick;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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

public class Resources {

    // UI

    public static Button.ButtonStyle BUTTON_STYLE;
    public static TextureRegion CROSS_TEXTURE;
    public static TextButton.TextButtonStyle TEXT_BUTTON_STYLE;
    public static TextField.TextFieldStyle TEXT_FIELD_STYLE;
    public static Label.LabelStyle LABEL_STYLE;
    public static ThumbStick.ThumbStickStyle THUMB_STICK_STYLE;
    public static HotBarSlot.HotBarSlotStyle HOT_BAR_SLOT_STYLE_TOOL;
    public static HotBarSlot.HotBarSlotStyle HOT_BAR_SLOT_STYLE_BUILD;
    public static HotBarSlot.HotBarSlotStyle HOT_BAR_SLOT_STYLE_USE;
    public static SwitchArrow.SwitchArrowStyle SWITCH_ARROW_STYLE;

    public static BitmapFont ARIAL_FONT;

    // animations

    public static Animation<TextureRegion> PLAYER_IDLE_ANIM_R;
    public static Animation<TextureRegion> PLAYER_IDLE_ANIM_L;
    public static Animation<TextureRegion> PLAYER_RUN_ANIM_R;
    public static Animation<TextureRegion> PLAYER_RUN_ANIM_L;

    public static Animation<TextureRegion> TILE_BREAK_ANIM;

    // tiles

    public static TextureRegion STONE_TEXTURE;
    public static TextureRegion GRASS_TEXTURE;
    public static TextureRegion DIRT_TEXTURE;
    public static TextureRegion WOOD_TEXTURE;
    public static TextureRegion TREE_TOP_TEXTURE;
    public static TextureRegion WHITE_TILE;
    public static TextureRegion CAMPFIRE_TEXTURE;

    // items
    public static TextureRegion PICKAXE_TEXTURE;

    // inventory / grid
    public static TextureRegion INVENTORY_TEXTURE;
    public static TextureRegion INV_TILE_TEX;
    public static TextureRegion RECIPE_BACK;

    public static void loadTextures() {

        TextureAtlas ga = new TextureAtlas("game_assets.atlas");
        TextureAtlas uia = new TextureAtlas("ui/gui_assets.atlas");

        ARIAL_FONT = new BitmapFont(Gdx.files.internal("ui/arial_big.fnt"));

        // UI
        Drawable up = new TextureRegionDrawable(uia.findRegion("button_base"));
        Drawable down = new TextureRegionDrawable(uia.findRegion("button_pressed"));
        Drawable selection = new TextureRegionDrawable(uia.findRegion("selection"));
        Drawable cursor = new TextureRegionDrawable(uia.findRegion("cursor"));
        TextureRegion tsBack = uia.findRegion("thumb_stick_back");
        TextureRegion tsFore = uia.findRegion("thumb_stick_fore");
        TextureRegion hbsBack = uia.findRegion("hot_bar_slot_background");
        TextureRegion hbsFrame = uia.findRegion("hot_bar_slot_frame");
        TextureRegion hbsDefaultTool = uia.findRegion("empty_hotbar_tools");
        TextureRegion switchArrowUp = uia.findRegion("switch_arrow_up");
        TextureRegion switchArrowUpPressed = uia.findRegion("switch_arrow_up_pressed");
        CROSS_TEXTURE = uia.findRegion("cross");

        BUTTON_STYLE = new Button.ButtonStyle(up, down, up);
        TEXT_BUTTON_STYLE = new TextButton.TextButtonStyle(up, down, up, ARIAL_FONT);
        TEXT_FIELD_STYLE = new TextField.TextFieldStyle(ARIAL_FONT, Color.WHITE, cursor, selection, up);
        LABEL_STYLE = new Label.LabelStyle(ARIAL_FONT, Color.WHITE);
        THUMB_STICK_STYLE = new ThumbStick.ThumbStickStyle(tsBack, tsFore);
        HOT_BAR_SLOT_STYLE_TOOL = new HotBarSlot.HotBarSlotStyle(hbsBack, hbsFrame, hbsDefaultTool);
        SWITCH_ARROW_STYLE = new SwitchArrow.SwitchArrowStyle(switchArrowUp, switchArrowUpPressed);

        // animations
        Animation.PlayMode pm = Animation.PlayMode.LOOP_PINGPONG;
        PLAYER_IDLE_ANIM_R = new Animation<>(0.5f, ga.findRegions("player_right_idle"), pm);
        PLAYER_IDLE_ANIM_L = new Animation<>(0.5f, ga.findRegions("player_left_idle"), pm);
        PLAYER_RUN_ANIM_R = new Animation<>(0.1f, ga.findRegions("player_right_run"), pm);
        PLAYER_RUN_ANIM_L = new Animation<>(0.1f, ga.findRegions("player_left_run"), pm);

        TILE_BREAK_ANIM = new Animation<>(0.34f, ga.findRegions("tile_break"), pm);

        // tiles
        STONE_TEXTURE = ga.findRegion("stone");
        GRASS_TEXTURE = ga.findRegion("grass");
        DIRT_TEXTURE = ga.findRegion("dirt");
        WOOD_TEXTURE = ga.findRegion("wood");
        TREE_TOP_TEXTURE = ga.findRegion("tree_top");
        WHITE_TILE = ga.findRegion("white_tile");
        CAMPFIRE_TEXTURE = ga.findRegion("campfire");

        // items
        PICKAXE_TEXTURE  = ga.findRegion("pickaxe");

        // inventory / grid
        INVENTORY_TEXTURE = ga.findRegion("inventory");
        INV_TILE_TEX = uia.findRegion("inv_tile");
        RECIPE_BACK = uia.findRegion("recipe_list_background");

    }

    public static void dispose() {
        ARIAL_FONT.dispose();
    }

}
