package com.astetyne.main;

import com.astetyne.main.gui.HotBarSlot;
import com.astetyne.main.gui.SwitchArrow;
import com.astetyne.main.gui.ThumbStick;
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

    public static void loadTextures() {

        TextureAtlas ga = new TextureAtlas("game_assets.atlas");
        TextureAtlas uia = new TextureAtlas("ui/gui_assets.atlas");

        ARIAL_FONT = new BitmapFont(Gdx.files.internal("ui/arial_big.fnt"));

        // UI
        Drawable drd = new TextureRegionDrawable(uia.findRegion("default-round-down"));
        Drawable dr = new TextureRegionDrawable(uia.findRegion("default-round"));
        Drawable selection = new TextureRegionDrawable(uia.findRegion("selection"));
        Drawable textfield = new TextureRegionDrawable(uia.findRegion("textfield"));
        Drawable cursor = new TextureRegionDrawable(uia.findRegion("cursor"));
        TextureRegion tsBack = uia.findRegion("thumb_stick_background");
        TextureRegion tsFore = uia.findRegion("thumb_stick_foreground");
        TextureRegion hbsBack = uia.findRegion("hot_bar_slot_background");
        TextureRegion hbsFrame = uia.findRegion("hot_bar_slot_frame");
        TextureRegion hbsDefaultTool = uia.findRegion("default_item_tool");

        BUTTON_STYLE = new Button.ButtonStyle(drd, dr, drd);
        TEXT_BUTTON_STYLE = new TextButton.TextButtonStyle(dr, drd, dr, ARIAL_FONT);
        TEXT_FIELD_STYLE = new TextField.TextFieldStyle(ARIAL_FONT, Color.WHITE, cursor, selection, textfield);
        LABEL_STYLE = new Label.LabelStyle(ARIAL_FONT, Color.WHITE);
        THUMB_STICK_STYLE = new ThumbStick.ThumbStickStyle(tsBack, tsFore);
        HOT_BAR_SLOT_STYLE_TOOL = new HotBarSlot.HotBarSlotStyle(hbsBack, hbsFrame, hbsDefaultTool);
        SWITCH_ARROW_STYLE = new SwitchArrow.SwitchArrowStyle(tsFore, tsBack);

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
    }

    public static void dispose() {
        ARIAL_FONT.dispose();
    }

}
