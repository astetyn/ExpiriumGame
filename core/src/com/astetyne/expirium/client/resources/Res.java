package com.astetyne.expirium.client.resources;

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

    public static NinePatchDrawable FRAME_ROUND, FRAME_SQUARE, FRAME_ROUND_GRAY, FRAME_ROUND_GRAY_LIGHT, FRAME_ROUND_GREEN, FRAME_ROUND_YELLOW,
        FRAME_SQUARE_GRAY, FRAME_ROUND_GRAY_TRANSP, INV_CELL;

    // background
    public static NinePatchDrawable BG_1, BG_2, BG_3;
    public static TextureRegion SUN, MOON, STAR, RAIN_DROP;

    // gui
    public static BitmapFont MAIN_FONT, TITLE_FONT, WARN_FONT, WORLD_FONT;

    public static Button.ButtonStyle BUTTON_STYLE;
    public static TextureRegion CROSS_ICON;
    public static TextButton.TextButtonStyle TEXT_BUTTON_STYLE;
    public static TextField.TextFieldStyle TEXT_FIELD_STYLE;
    public static Label.LabelStyle LABEL_STYLE, TITLE_LABEL_STYLE, WARN_LABEL_STYLE;
    public static ThumbStick.ThumbStickStyle THUMB_STICK_STYLE;
    public static MoveThumbStick.MoveThumbStickStyle MOVE_THUMB_STICK_STYLE;
    public static TextureRegion ARROW;
    public static TextureRegion DAMAGE_OVERLAP;

    // entities
    public static Animation<TextureRegion> SQUIRREL_IDLE;

    public static void loadTextures(TextureAtlas textures, TextureAtlas background) {

        //background
        BG_1 = new NinePatchDrawable(background.createPatch("background_1"));
        BG_2 = new NinePatchDrawable(background.createPatch("background_2"));
        BG_3 = new NinePatchDrawable(background.createPatch("background_3"));
        BG_1.getPatch().scale(0, 2);
        BG_2.getPatch().scale(0, 2);
        BG_3.getPatch().scale(0, 2);
        SUN = background.findRegion("sun");
        MOON = background.findRegion("moon");
        STAR = background.findRegion("star");
        RAIN_DROP = textures.findRegion("rain_drop");

        // fonts
        MAIN_FONT = new BitmapFont(Gdx.files.internal("main_font.fnt"), textures.findRegion("main_font"));
        TITLE_FONT = new BitmapFont(Gdx.files.internal("main_font.fnt"), textures.findRegion("main_font"));
        TITLE_FONT.getData().setScale(2);
        WARN_FONT = new BitmapFont(Gdx.files.internal("main_font.fnt"), textures.findRegion("main_font"));
        WARN_FONT.getData().setScale(1.6f);
        WORLD_FONT = new BitmapFont(Gdx.files.internal("main_font.fnt"), textures.findRegion("main_font"));
        WORLD_FONT.getData().setScale(0.01f);

        MAIN_FONT.setUseIntegerPositions(false);
        WORLD_FONT.setUseIntegerPositions(false);

        Color gray = new Color(110f/255, 110f/255, 110f/255, 1);
        Color textFieldColor = new Color(180f/255, 180f/255, 150f/255, 1);
        Color yellow = new Color(132f/255, 118f/255, 55f/255, 1);
        Color green = new Color(61f/255, 102f/255, 42f/255, 1);
        Color transpGray = new Color(Color.GRAY);
        transpGray.a = 0.4f;

        // ninepatch
        FRAME_ROUND = new NinePatchDrawable(textures.createPatch("frame_round"));
        FRAME_ROUND.getPatch().scale(10, 10);
        FRAME_ROUND_GRAY = FRAME_ROUND.tint(gray);
        FRAME_ROUND_GRAY_LIGHT = FRAME_ROUND.tint(textFieldColor);
        FRAME_ROUND_GREEN = FRAME_ROUND.tint(green);
        FRAME_ROUND_YELLOW = FRAME_ROUND.tint(yellow);
        FRAME_ROUND_GRAY_TRANSP = FRAME_ROUND.tint(transpGray);
        FRAME_SQUARE = new NinePatchDrawable(textures.createPatch("frame_square"));
        FRAME_SQUARE.getPatch().scale(10, 10);
        FRAME_SQUARE_GRAY = FRAME_SQUARE.tint(Color.LIGHT_GRAY);
        INV_CELL = new NinePatchDrawable(textures.createPatch("inv_cell"));
        INV_CELL.getPatch().scale((1f/16)*Consts.INV_TILE_WIDTH, (1f/16) * Utils.percFromW(Consts.INV_TILE_WIDTH));

        Drawable selection = new TextureRegionDrawable(textures.findRegion("selection"));
        Drawable cursor = new TextureRegionDrawable(textures.findRegion("cursor"));
        TextureRegion tsBack = textures.findRegion("thumb_stick_back");
        TextureRegion tsFore = textures.findRegion("thumb_stick_fore");
        ARROW = textures.findRegion("switch_arrow");
        DAMAGE_OVERLAP = textures.findRegion("damage_overlap");
        TextureRegion moveTsSideArrow = textures.findRegion("ts_move_arrow_side");
        TextureRegion moveTsUpArrow = textures.findRegion("ts_move_arrow_up");

        CROSS_ICON = textures.findRegion("cross_icon");

        BUTTON_STYLE = new Button.ButtonStyle(FRAME_ROUND_GRAY, FRAME_ROUND_GREEN, FRAME_ROUND_GRAY);
        TEXT_BUTTON_STYLE = new TextButton.TextButtonStyle(FRAME_ROUND_GRAY, FRAME_ROUND_GREEN, FRAME_ROUND_GRAY, MAIN_FONT);
        TEXT_FIELD_STYLE = new TextField.TextFieldStyle(MAIN_FONT, Color.WHITE, cursor, selection, FRAME_ROUND_GRAY_LIGHT);
        LABEL_STYLE = new Label.LabelStyle(MAIN_FONT, Color.WHITE);
        WARN_LABEL_STYLE = new Label.LabelStyle(WARN_FONT, Color.WHITE);
        TITLE_LABEL_STYLE = new Label.LabelStyle(TITLE_FONT, Color.WHITE);
        THUMB_STICK_STYLE = new ThumbStick.ThumbStickStyle(tsBack, tsFore);
        MOVE_THUMB_STICK_STYLE = new MoveThumbStick.MoveThumbStickStyle(tsFore, moveTsSideArrow, moveTsUpArrow);

        // entities
        SQUIRREL_IDLE = new Animation<>(1, textures.findRegion("squirrel_idle"));

    }

    public static void dispose() {
        MAIN_FONT.dispose();
    }

}
