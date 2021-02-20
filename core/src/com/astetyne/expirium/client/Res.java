package com.astetyne.expirium.client;

import com.astetyne.expirium.client.gui.widget.BaseGrid;
import com.astetyne.expirium.client.gui.widget.MoveThumbStick;
import com.astetyne.expirium.client.gui.widget.ThumbStick;
import com.astetyne.expirium.client.resources.GuiRes;
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

public class Res {

    // gui
    public static BitmapFont MAIN_FONT, TITLE_FONT, WORLD_FONT;

    public static Button.ButtonStyle BUTTON_STYLE;
    public static TextureRegion CROSS_ICON;
    public static TextButton.TextButtonStyle TEXT_BUTTON_STYLE;
    public static TextField.TextFieldStyle TEXT_FIELD_STYLE;
    public static Label.LabelStyle LABEL_STYLE, TITLE_LABEL_STYLE;
    public static ThumbStick.ThumbStickStyle THUMB_STICK_STYLE;
    public static MoveThumbStick.MoveThumbStickStyle MOVE_THUMB_STICK_STYLE;
    public static BaseGrid.BaseGridStyle BASE_GRID_STYLE;
    public static TextureRegion ARROW;

    // entities
    public static Animation<TextureRegion> PLAYER_IDLE_ANIM;
    public static Animation<TextureRegion> PLAYER_MOVE_ANIM;
    public static Animation<TextureRegion> PLAYER_INTERACT_ANIM;

    public static Animation<TextureRegion> SQUIRREL_IDLE;

    public static void loadTextures(TextureAtlas gui, TextureAtlas ent) {

        Animation.PlayMode loop_pong = Animation.PlayMode.LOOP_PINGPONG;

        /*FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("LadylikeBB.ttf"));
        //FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("Life is goofy.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 70;
        TITLE_FONT = gen.generateFont(parameter);
        TITLE_FONT.getData().setScale(2);
        parameter.size = 70;
        MAIN_FONT = gen.generateFont(parameter);
        /*gen.dispose();*/

        // fonts
        MAIN_FONT = new BitmapFont(Gdx.files.internal("main_font.fnt"), gui.findRegion("main_font"));
        TITLE_FONT = new BitmapFont(Gdx.files.internal("main_font.fnt"), gui.findRegion("main_font"));
        TITLE_FONT.getData().setScale(2);
        WORLD_FONT = new BitmapFont(Gdx.files.internal("main_font.fnt"), gui.findRegion("main_font"));
        WORLD_FONT.getData().setScale(0.01f);

        MAIN_FONT.setUseIntegerPositions(false);
        WORLD_FONT.setUseIntegerPositions(false);

        Drawable selection = new TextureRegionDrawable(gui.findRegion("selection"));
        Drawable cursor = new TextureRegionDrawable(gui.findRegion("cursor"));
        TextureRegion tsBack = gui.findRegion("thumb_stick_back");
        TextureRegion tsFore = gui.findRegion("thumb_stick_fore");
        ARROW = gui.findRegion("switch_arrow");
        TextureRegion invTile = gui.findRegion("inv_tile");
        TextureRegion invTileSplit = gui.findRegion("inv_tile_split");
        TextureRegion invTileThrow = gui.findRegion("trash_icon");
        TextureRegion moveTsSideArrow = gui.findRegion("ts_move_arrow_side");
        TextureRegion moveTsUpArrow = gui.findRegion("ts_move_arrow_up");

        CROSS_ICON = gui.findRegion("cross_icon");

        BUTTON_STYLE = new Button.ButtonStyle(GuiRes.FRAME_GRAY.getDrawable(), GuiRes.FRAME_GREEN.getDrawable(), GuiRes.FRAME_GRAY.getDrawable());
        TEXT_BUTTON_STYLE = new TextButton.TextButtonStyle(GuiRes.FRAME_GRAY.getDrawable(), GuiRes.FRAME_GREEN.getDrawable(), GuiRes.FRAME_GRAY.getDrawable(), MAIN_FONT);
        TEXT_FIELD_STYLE = new TextField.TextFieldStyle(MAIN_FONT, Color.WHITE, cursor, selection, GuiRes.FRAME_GRAY.getDrawable());
        LABEL_STYLE = new Label.LabelStyle(MAIN_FONT, Color.WHITE);
        TITLE_LABEL_STYLE = new Label.LabelStyle(TITLE_FONT, Color.WHITE);
        THUMB_STICK_STYLE = new ThumbStick.ThumbStickStyle(tsBack, tsFore);
        MOVE_THUMB_STICK_STYLE = new MoveThumbStick.MoveThumbStickStyle(tsFore, moveTsSideArrow, moveTsUpArrow);
        BASE_GRID_STYLE = new BaseGrid.BaseGridStyle(invTile, invTileThrow, invTileSplit);

        // entities
        PLAYER_IDLE_ANIM = new Animation<>(0.5f, ent.findRegions("player_idle"), loop_pong);
        PLAYER_MOVE_ANIM = new Animation<>(0.05f, ent.findRegions("player_move"), loop_pong);
        PLAYER_INTERACT_ANIM = new Animation<>(0.12f, ent.findRegions("player_interact"), Animation.PlayMode.LOOP);

        SQUIRREL_IDLE = new Animation<>(1, ent.findRegion("squirrel_idle"));

    }

    public static void dispose() {
        MAIN_FONT.dispose();
    }

}
