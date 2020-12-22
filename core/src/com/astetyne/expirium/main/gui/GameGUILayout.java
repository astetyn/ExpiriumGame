package com.astetyne.expirium.main.gui;

import com.astetyne.expirium.main.Resources;
import com.astetyne.expirium.main.stages.GameStage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import static com.astetyne.expirium.main.stages.GameStage.toPixels;

public class GameGUILayout {

    private final Table mainTable;
    private final Table itemSelectTable;

    private final ThumbStick movementTS, breakTS;
    private final Label fpsLabel, locationLabel, entityLabel, warnLabel;

    public GameGUILayout() {

        mainTable = new Table();
        itemSelectTable = new Table();

        movementTS = new ThumbStick(Resources.THUMB_STICK_STYLE);
        breakTS = new ThumbStick(Resources.THUMB_STICK_STYLE);

        fpsLabel = new Label("", Resources.LABEL_STYLE);
        fpsLabel.setFontScale(0.4f * Gdx.graphics.getDensity());
        locationLabel = new Label("", Resources.LABEL_STYLE);
        locationLabel.setFontScale(0.4f * Gdx.graphics.getDensity());
        entityLabel = new Label("", Resources.LABEL_STYLE);
        entityLabel.setFontScale(0.4f * Gdx.graphics.getDensity());
        warnLabel = new Label("pre-alpha", Resources.LABEL_STYLE);
        warnLabel.setFontScale(0.4f * Gdx.graphics.getDensity());
        warnLabel.setColor(1,0.1f,0.1f,1);

        itemSelectTable.row();
        itemSelectTable.add(GameStage.get().getInv().getSwitchArrowUp()).padBottom(toPixels(6)).colspan(3);
        itemSelectTable.row();
        itemSelectTable.add(GameStage.get().getInv().getToolSlot()).padRight(toPixels(15));
        itemSelectTable.add(GameStage.get().getInv().getMaterialSlot()).padRight(toPixels(15));
        itemSelectTable.add(GameStage.get().getInv().getConsumableSlot());
        itemSelectTable.row();
        itemSelectTable.add(GameStage.get().getInv().getSwitchArrowDown()).padTop(toPixels(6)).colspan(3);

        buildTableTool();

    }

    public void update() {

        fpsLabel.setText("fps: "+Gdx.graphics.getFramesPerSecond());
        Vector2 loc = GameStage.get().getWorld().getPlayer().getLocation();
        locationLabel.setText("x: "+((int)loc.x)+" y: "+((int)loc.y));
        entityLabel.setText("entities: "+GameStage.get().getWorld().getEntitiesID().keySet().size());

    }

    public void buildTableTool() {
        preBuildTable();
        mainTable.add(breakTS).align(Align.bottomRight).padBottom(toPixels(30)).padRight(toPixels(30)).uniformX();
    }

    public void buildTableBuild() {
        preBuildTable();
        mainTable.add().align(Align.bottomRight).padBottom(toPixels(30)).padRight(toPixels(30)).uniformX();
    }

    public void buildTableUse() {
        preBuildTable();
        mainTable.add().align(Align.bottomRight).padBottom(toPixels(30)).padRight(toPixels(30)).uniformX();
    }

    private void preBuildTable() {

        mainTable.setDebug(true);
        mainTable.setFillParent(true);

        mainTable.clear();

        mainTable.row().expandX();
        mainTable.add(fpsLabel).colspan(3).align(Align.left);
        mainTable.row();
        mainTable.add(locationLabel).colspan(3).align(Align.left);
        mainTable.row();
        mainTable.add(entityLabel).colspan(3).align(Align.left);
        mainTable.row();
        mainTable.add(warnLabel).colspan(3).align(Align.left);
        mainTable.row().expand();
        mainTable.add(movementTS).align(Align.bottomLeft).padBottom(toPixels(30)).padLeft(toPixels(30)).uniformX();
        mainTable.add(itemSelectTable).align(Align.bottom).padBottom(toPixels(6));

    }

    public Table getTable() {
        return mainTable;
    }

    public ThumbStick getMovementTS() {
        return movementTS;
    }

    public ThumbStick getBreakTS() {
        return breakTS;
    }
}
