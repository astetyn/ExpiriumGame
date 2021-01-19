package com.astetyne.expirium.client;

import com.astetyne.expirium.client.gui.widget.BaseGrid;
import com.astetyne.expirium.client.gui.widget.SwitchArrow;
import com.astetyne.expirium.client.gui.widget.ThumbStick;
import com.astetyne.expirium.client.resources.GuiRes;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Res {

    // gui
    public static BitmapFont TITLE_FONT;
    public static BitmapFont MAIN_FONT;

    public static Button.ButtonStyle BUTTON_STYLE;
    public static TextureRegion CROSS_ICON;
    public static TextButton.TextButtonStyle TEXT_BUTTON_STYLE;
    public static TextField.TextFieldStyle TEXT_FIELD_STYLE;
    public static Label.LabelStyle LABEL_STYLE, TITLE_LABEL_STYLE;
    public static ThumbStick.ThumbStickStyle THUMB_STICK_STYLE;
    public static SwitchArrow.SwitchArrowStyle SWITCH_ARROW_STYLE;
    public static BaseGrid.BaseGridStyle BASE_GRID_STYLE;

    // entities
    public static Animation<TextureRegion> PLAYER_IDLE_ANIM_R;
    public static Animation<TextureRegion> PLAYER_IDLE_ANIM_L;
    public static Animation<TextureRegion> PLAYER_RUN_ANIM_R;
    public static Animation<TextureRegion> PLAYER_RUN_ANIM_L;

    public static void loadTextures() {

        Animation.PlayMode loop_pong = Animation.PlayMode.LOOP_PINGPONG;

        TextureAtlas gui = new TextureAtlas("gui.atlas");
        TextureAtlas ent = new TextureAtlas("entities.atlas");
        TextureAtlas bg = new TextureAtlas("background.atlas");

        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("ladylikeBB.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 90;
        TITLE_FONT = gen.generateFont(parameter);
        parameter.size = 60;
        MAIN_FONT = gen.generateFont(parameter);
        gen.dispose();

        // gui
        //MAIN_FONT = new BitmapFont(Gdx.files.internal("arial_medium.fnt"), gui.findRegion("arial_medium"));
        //MAIN_FONT = new BitmapFont(Gdx.files.internal("test1.fnt"));
        //MAIN_FONT.getData().setScale((float)Gdx.graphics.getHeight() / Gdx.graphics.getWidth(), 1);
        //MAIN_FONT.getData().setScale(1, Gdx.graphics.getWidth() / (float)Gdx.graphics.getHeight());

        //TITLE_FONT = new BitmapFont(Gdx.files.internal("arial_medium.fnt"), gui.findRegion("arial_medium"));
        //TITLE_FONT.getData().setScale((float)Gdx.graphics.getHeight() * 3/ Gdx.graphics.getWidth(), 3);
        //TITLE_FONT.getData().scale(3);

        Drawable selection = new TextureRegionDrawable(gui.findRegion("selection"));
        Drawable cursor = new TextureRegionDrawable(gui.findRegion("cursor"));
        TextureRegion tsBack = gui.findRegion("thumb_stick_back");
        TextureRegion tsFore = gui.findRegion("thumb_stick_fore");
        TextureRegion hbsFrame = gui.findRegion("hot_bar_slot_frame");
        TextureRegion switchArrowUp = gui.findRegion("switch_arrow_up");
        TextureRegion switchArrowUpPressed = gui.findRegion("switch_arrow_up_pressed");
        TextureRegion invTile = gui.findRegion("inv_tile");
        TextureRegion invTileSplit = gui.findRegion("inv_tile_split");
        TextureRegion invTileThrow = gui.findRegion("trash_icon");

        CROSS_ICON = gui.findRegion("cross_icon");

        BUTTON_STYLE = new Button.ButtonStyle(GuiRes.FRAME_GRAY.getDrawable(), GuiRes.FRAME_GREEN.getDrawable(), GuiRes.FRAME_GRAY.getDrawable());
        TEXT_BUTTON_STYLE = new TextButton.TextButtonStyle(GuiRes.FRAME_GRAY.getDrawable(), GuiRes.FRAME_GREEN.getDrawable(), GuiRes.FRAME_GRAY.getDrawable(), MAIN_FONT);
        TEXT_FIELD_STYLE = new TextField.TextFieldStyle(MAIN_FONT, Color.WHITE, cursor, selection, GuiRes.FRAME_GRAY.getDrawable());
        LABEL_STYLE = new Label.LabelStyle(MAIN_FONT, Color.WHITE);
        TITLE_LABEL_STYLE = new Label.LabelStyle(TITLE_FONT, Color.WHITE);
        THUMB_STICK_STYLE = new ThumbStick.ThumbStickStyle(tsBack, tsFore);
        SWITCH_ARROW_STYLE = new SwitchArrow.SwitchArrowStyle(switchArrowUp, switchArrowUpPressed);
        BASE_GRID_STYLE = new BaseGrid.BaseGridStyle(invTile, invTileThrow, invTileSplit);

        // entities
        PLAYER_IDLE_ANIM_R = new Animation<>(0.5f, ent.findRegions("player_right_idle"), loop_pong);
        PLAYER_IDLE_ANIM_L = new Animation<>(0.5f, ent.findRegions("player_left_idle"), loop_pong);
        PLAYER_RUN_ANIM_R = new Animation<>(0.1f, ent.findRegions("player_right_run"), loop_pong);
        PLAYER_RUN_ANIM_L = new Animation<>(0.1f, ent.findRegions("player_left_run"), loop_pong);

    }

    public static void dispose() {
        MAIN_FONT.dispose();
    }

}
