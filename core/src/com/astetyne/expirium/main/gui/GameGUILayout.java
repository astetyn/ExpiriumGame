package com.astetyne.expirium.main.gui;

import com.astetyne.expirium.main.Resources;
import com.astetyne.expirium.main.stages.GameStage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import static com.astetyne.expirium.main.stages.GameStage.toPixels;

public class GameGUILayout extends GUILayout {

    private final Table rootTable, itemSelectTable, debugInfoTable, playerStatsTable;

    private final Label fpsLabel, locationLabel, entityLabel, warnLabel, healthStat, hungerStat, tempStat;
    private final Image healthImage, hungerImage, tempImage;

    public GameGUILayout() {

        rootTable = new Table();
        itemSelectTable = new Table();
        debugInfoTable = new Table();
        playerStatsTable = new Table();

        fpsLabel = new Label("", Resources.LABEL_STYLE);
        fpsLabel.setFontScale(0.4f * Gdx.graphics.getDensity());
        locationLabel = new Label("", Resources.LABEL_STYLE);
        locationLabel.setFontScale(0.4f * Gdx.graphics.getDensity());
        entityLabel = new Label("", Resources.LABEL_STYLE);
        entityLabel.setFontScale(0.4f * Gdx.graphics.getDensity());
        warnLabel = new Label("pre-alpha", Resources.LABEL_STYLE);
        warnLabel.setFontScale(0.4f * Gdx.graphics.getDensity());
        warnLabel.setColor(1,0.1f,0.1f,1);

        healthImage = new Image(Resources.TREE_TOP_TEXTURE);
        hungerImage = new Image(Resources.TREE_TOP_TEXTURE);
        tempImage = new Image(Resources.TREE_TOP_TEXTURE);

        healthStat = new Label("0%", Resources.LABEL_STYLE);
        healthStat.setFontScale(0.3f);
        hungerStat = new Label("0%", Resources.LABEL_STYLE);
        hungerStat.setFontScale(0.3f);
        tempStat = new Label("0%", Resources.LABEL_STYLE);
        tempStat.setFontScale(0.3f);

        itemSelectTable.add(GameStage.get().getInv().getSwitchArrowUp()).padBottom(toPixels(6)).colspan(3);
        itemSelectTable.row();
        itemSelectTable.add(GameStage.get().getInv().getToolSlot()).padRight(toPixels(15));
        itemSelectTable.add(GameStage.get().getInv().getMaterialSlot()).padRight(toPixels(15));
        itemSelectTable.add(GameStage.get().getInv().getConsumableSlot());
        itemSelectTable.row();
        itemSelectTable.add(GameStage.get().getInv().getSwitchArrowDown()).padTop(toPixels(6)).colspan(3);

        debugInfoTable.add(fpsLabel).left();
        debugInfoTable.row();
        debugInfoTable.add(locationLabel).left();
        debugInfoTable.row();
        debugInfoTable.add(entityLabel).left();
        debugInfoTable.row();
        debugInfoTable.add(warnLabel).left();

        int iconSize = toPixels(30);
        playerStatsTable.add(healthStat).padTop(5);
        playerStatsTable.add(healthImage).height(iconSize).width(iconSize);
        playerStatsTable.row();
        playerStatsTable.add(hungerStat).padTop(5);
        playerStatsTable.add(hungerImage).height(iconSize).width(iconSize);
        playerStatsTable.row();
        playerStatsTable.add(tempStat).padTop(5);
        playerStatsTable.add(tempImage).height(iconSize).width(iconSize);
        playerStatsTable.row();

        rootTable.debugCell();
        //mainTable.setDebug(true);

    }

    @Override
    public void update() {
        fpsLabel.setText("fps: "+Gdx.graphics.getFramesPerSecond());
        Vector2 loc = GameStage.get().getWorld().getPlayer().getLocation();
        locationLabel.setText("x: "+((int)loc.x)+" y: "+((int)loc.y));
        entityLabel.setText("entities: "+GameStage.get().getWorld().getEntitiesID().keySet().size());
    }

    @Override
    public Table getRootTable() {
        return rootTable;
    }

    @Override
    public void build(Stage stage) {
        buildTableTool();
        stage.clear();
        stage.addActor(rootTable);
    }

    public void buildTableTool() {
        preBuildTable();
        ThumbStick breakTS = GameStage.get().getWorld().getPlayer().getTileBreaker().getBreakTS();
        rootTable.add(breakTS).align(Align.bottomRight).padBottom(toPixels(30)).padRight(toPixels(30)).uniformX();
    }

    public void buildTableBuild() {
        preBuildTable();
        ImageButton but = GameStage.get().getWorld().getPlayer().getTilePlacer().getStabilityButton();
        rootTable.add(but).align(Align.bottomRight).padBottom(toPixels(30)).padRight(toPixels(30)).uniformX();
    }

    public void buildTableUse() {
        preBuildTable();
        ImageButton but = GameStage.get().getInv().getConsumeButton();
        rootTable.add(but).align(Align.bottomRight).padBottom(toPixels(30)).padRight(toPixels(30)).uniformX();
    }

    private void preBuildTable() {

        rootTable.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        rootTable.clear();

        rootTable.add(debugInfoTable).align(Align.topLeft);
        rootTable.add();
        rootTable.add(playerStatsTable).align(Align.topRight);
        rootTable.row();
        rootTable.add(GameStage.get().getInv().getInventoryButton()).align(Align.right).colspan(3);
        rootTable.row().expand();
        ThumbStick movementTS = GameStage.get().getWorld().getPlayer().getMovementTS();
        rootTable.add(movementTS).align(Align.bottomLeft).padBottom(toPixels(30)).padLeft(toPixels(30)).uniformX();
        rootTable.add(itemSelectTable).align(Align.bottom).padBottom(toPixels(6));

    }
}
