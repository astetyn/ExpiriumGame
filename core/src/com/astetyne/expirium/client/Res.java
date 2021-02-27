package com.astetyne.expirium.client;

import com.astetyne.expirium.client.gui.widget.BaseGrid;
import com.astetyne.expirium.client.gui.widget.MoveThumbStick;
import com.astetyne.expirium.client.gui.widget.ThumbStick;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.Utils;
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
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Res {

    public static NinePatchDrawable FRAME_ROUND, FRAME_SQUARE, FRAME_ROUND_GRAY, FRAME_ROUND_GREEN, FRAME_ROUND_YELLOW,
        FRAME_SQUARE_GRAY, FRAME_ROUND_GRAY_TRANSP, INV_TILE;

    // gui
    public static BitmapFont MAIN_FONT, TITLE_FONT, WARN_FONT, WORLD_FONT;

    public static Button.ButtonStyle BUTTON_STYLE;
    public static TextureRegion CROSS_ICON;
    public static TextButton.TextButtonStyle TEXT_BUTTON_STYLE;
    public static TextField.TextFieldStyle TEXT_FIELD_STYLE;
    public static Label.LabelStyle LABEL_STYLE, TITLE_LABEL_STYLE, WARN_LABEL_STYLE;
    public static ThumbStick.ThumbStickStyle THUMB_STICK_STYLE;
    public static MoveThumbStick.MoveThumbStickStyle MOVE_THUMB_STICK_STYLE;
    public static BaseGrid.BaseGridStyle BASE_GRID_STYLE;
    public static TextureRegion ARROW;
    public static TextureRegion DAMAGE_OVERLAP;

    // entities
    public static Animation<TextureRegion> PLAYER_IDLE_ANIM;
    public static Animation<TextureRegion> PLAYER_MOVE_ANIM;
    public static Animation<TextureRegion> PLAYER_INTERACT_ANIM;

    public static Animation<TextureRegion> SQUIRREL_IDLE;

    public static void loadTextures(TextureAtlas atlas) {

        Animation.PlayMode loop_pong = Animation.PlayMode.LOOP_PINGPONG;

        // fonts
        MAIN_FONT = new BitmapFont(Gdx.files.internal("main_font.fnt"), atlas.findRegion("main_font"));
        TITLE_FONT = new BitmapFont(Gdx.files.internal("main_font.fnt"), atlas.findRegion("main_font"));
        TITLE_FONT.getData().setScale(2);
        WARN_FONT = new BitmapFont(Gdx.files.internal("main_font.fnt"), atlas.findRegion("main_font"));
        WARN_FONT.getData().setScale(1.6f);
        WORLD_FONT = new BitmapFont(Gdx.files.internal("main_font.fnt"), atlas.findRegion("main_font"));
        WORLD_FONT.getData().setScale(0.01f);

        MAIN_FONT.setUseIntegerPositions(false);
        WORLD_FONT.setUseIntegerPositions(false);

        Color gray = new Color(110f/255, 110f/255, 110f/255, 1);
        Color yellow = new Color(132f/255, 118f/255, 55f/255, 1);
        Color green = new Color(61f/255, 102f/255, 42f/255, 1);
        Color tranpGray = new Color(Color.GRAY);
        tranpGray.a = 0.4f;

        // ninepatch
        FRAME_ROUND = new NinePatchDrawable(atlas.createPatch("frame_round"));
        FRAME_ROUND.getPatch().scale(10, 10);
        FRAME_ROUND_GRAY = FRAME_ROUND.tint(gray);
        FRAME_ROUND_GREEN = FRAME_ROUND.tint(green);
        FRAME_ROUND_YELLOW = FRAME_ROUND.tint(yellow);
        FRAME_ROUND_GRAY_TRANSP = FRAME_ROUND.tint(tranpGray);
        FRAME_SQUARE = new NinePatchDrawable(atlas.createPatch("frame_square"));
        FRAME_SQUARE.getPatch().scale(10, 10);
        FRAME_SQUARE_GRAY = FRAME_SQUARE.tint(Color.LIGHT_GRAY);
        INV_TILE = new NinePatchDrawable(atlas.createPatch("inv_tile"));
        INV_TILE.getPatch().scale((1f/16)*Consts.INV_TILE_WIDTH, (1f/16) * Utils.percFromW(Consts.INV_TILE_WIDTH));

        Drawable selection = new TextureRegionDrawable(atlas.findRegion("selection"));
        Drawable cursor = new TextureRegionDrawable(atlas.findRegion("cursor"));
        TextureRegion tsBack = atlas.findRegion("thumb_stick_back");
        TextureRegion tsFore = atlas.findRegion("thumb_stick_fore");
        ARROW = atlas.findRegion("switch_arrow");
        DAMAGE_OVERLAP = atlas.findRegion("damage_overlap");
        TextureRegionDrawable invTileSplit = new TextureRegionDrawable(atlas.findRegion("inv_tile_split"));
        TextureRegionDrawable invTileSplitHalf = new TextureRegionDrawable(atlas.findRegion("inv_tile_split_half"));
        TextureRegionDrawable invTileThrow = new TextureRegionDrawable(atlas.findRegion("trash_icon"));
        TextureRegion moveTsSideArrow = atlas.findRegion("ts_move_arrow_side");
        TextureRegion moveTsUpArrow = atlas.findRegion("ts_move_arrow_up");

        CROSS_ICON = atlas.findRegion("cross_icon");

        BUTTON_STYLE = new Button.ButtonStyle(FRAME_ROUND_GRAY, FRAME_ROUND_GREEN, FRAME_ROUND_GRAY);
        TEXT_BUTTON_STYLE = new TextButton.TextButtonStyle(FRAME_ROUND_GRAY, FRAME_ROUND_GREEN, FRAME_ROUND_GRAY, MAIN_FONT);
        TEXT_FIELD_STYLE = new TextField.TextFieldStyle(MAIN_FONT, Color.WHITE, cursor, selection, FRAME_ROUND_GRAY);
        LABEL_STYLE = new Label.LabelStyle(MAIN_FONT, Color.WHITE);
        WARN_LABEL_STYLE = new Label.LabelStyle(WARN_FONT, Color.WHITE);
        TITLE_LABEL_STYLE = new Label.LabelStyle(TITLE_FONT, Color.WHITE);
        THUMB_STICK_STYLE = new ThumbStick.ThumbStickStyle(tsBack, tsFore);
        MOVE_THUMB_STICK_STYLE = new MoveThumbStick.MoveThumbStickStyle(tsFore, moveTsSideArrow, moveTsUpArrow);
        BASE_GRID_STYLE = new BaseGrid.BaseGridStyle(INV_TILE, invTileThrow, invTileSplit, invTileSplitHalf);

        // entities
        PLAYER_IDLE_ANIM = new Animation<>(0.5f, atlas.findRegions("player_idle"), loop_pong);
        PLAYER_MOVE_ANIM = new Animation<>(0.05f, atlas.findRegions("player_move"), loop_pong);
        PLAYER_INTERACT_ANIM = new Animation<>(0.12f, atlas.findRegions("player_interact"), Animation.PlayMode.LOOP);

        SQUIRREL_IDLE = new Animation<>(1, atlas.findRegion("squirrel_idle"));

    }

    public static void dispose() {
        MAIN_FONT.dispose();
    }

}
