package com.astetyne.expirium.main.gui;

import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.stages.GameStage;
import com.astetyne.expirium.main.utils.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import static com.astetyne.expirium.main.utils.Utils.fromCMToPercW;

public class GameGUILayout extends GUILayout {

    private final Table rootTable, itemSelectTable, debugInfoTable, playerStatsTable;

    private final Label fpsLabel, locationLabel, entityLabel, warnLabel, healthStat, hungerStat, tempStat;
    private final Image healthImage, hungerImage, tempImage;

    public GameGUILayout() {

        rootTable = new Table();
        itemSelectTable = new Table();
        debugInfoTable = new Table();
        playerStatsTable = new Table();

        fpsLabel = new Label("", Res.LABEL_STYLE);
        locationLabel = new Label("", Res.LABEL_STYLE);
        entityLabel = new Label("", Res.LABEL_STYLE);
        warnLabel = new Label("pre-alpha", Res.LABEL_STYLE);
        warnLabel.setColor(1,0.1f,0.1f,1);

        healthImage = new Image(Res.TREE_TOP_TEXTURE);
        hungerImage = new Image(Res.TREE_TOP_TEXTURE);
        tempImage = new Image(Res.TREE_TOP_TEXTURE);

        healthStat = new Label("0%", Res.LABEL_STYLE);
        hungerStat = new Label("0%", Res.LABEL_STYLE);
        tempStat = new Label("0%", Res.LABEL_STYLE);

        itemSelectTable.add(GameStage.get().getInv().getSwitchArrowUp()).padBottom(10).colspan(3);
        itemSelectTable.row();
        itemSelectTable.add(GameStage.get().getInv().getToolSlot()).padRight(10);
        itemSelectTable.add(GameStage.get().getInv().getMaterialSlot()).padRight(10);
        itemSelectTable.add(GameStage.get().getInv().getConsumableSlot());
        itemSelectTable.row();
        itemSelectTable.add(GameStage.get().getInv().getSwitchArrowDown()).padTop(10).colspan(3);

        if(Constants.DEBUG) {
            debugInfoTable.add(fpsLabel).left();
            debugInfoTable.row();
            debugInfoTable.add(locationLabel).left();
            debugInfoTable.row();
            debugInfoTable.add(entityLabel).left();
            debugInfoTable.row();
        }
        debugInfoTable.add(warnLabel).left();

        float iconSize = 20;
        playerStatsTable.add(healthStat).padTop(10);
        playerStatsTable.add(healthImage).height(iconSize).width(iconSize);
        playerStatsTable.row();
        playerStatsTable.add(hungerStat).padTop(10);
        playerStatsTable.add(hungerImage).height(iconSize).width(iconSize);
        playerStatsTable.row();
        playerStatsTable.add(tempStat).padTop(10);
        playerStatsTable.add(tempImage).height(iconSize).width(iconSize);
        playerStatsTable.row();

        rootTable.setBounds(0, 0, 1000, 1000);

        if(Constants.DEBUG) rootTable.debugCell();
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

    @Override
    public void resize(int w, int h) {
        locationLabel.invalidateHierarchy();
        entityLabel.invalidateHierarchy();
        warnLabel.invalidateHierarchy();
    }

    public void buildTableTool() {
        preBuildTable();
        ThumbStick breakTS = GameStage.get().getWorld().getPlayer().getTileBreaker().getBreakTS();
        rootTable.add(breakTS).align(Align.bottomRight).padBottom(fromCMToPercW(1)).padRight(fromCMToPercW(1)).uniformX();
    }

    public void buildTableBuild() {
        preBuildTable();
        ImageButton but = GameStage.get().getWorld().getPlayer().getTilePlacer().getStabilityButton();
        rootTable.add(but).align(Align.bottomRight).padBottom(fromCMToPercW(1)).padRight(fromCMToPercW(1)).uniformX();
    }

    public void buildTableUse() {
        preBuildTable();
        ImageButton but = GameStage.get().getInv().getConsumeButton();
        rootTable.add(but).align(Align.bottomRight).padBottom(fromCMToPercW(1)).padRight(fromCMToPercW(1)).uniformX();
    }

    private void preBuildTable() {

        rootTable.clear();

        rootTable.add(debugInfoTable).align(Align.topLeft);
        rootTable.add();
        rootTable.add(playerStatsTable).align(Align.topRight);
        rootTable.row();
        rootTable.add(GameStage.get().getInv().getInventoryButton()).width(100).height(100).align(Align.right).colspan(3);
        rootTable.row().expand();
        ThumbStick movementTS = GameStage.get().getWorld().getPlayer().getMovementTS();
        rootTable.add(movementTS).align(Align.bottomLeft).padBottom(fromCMToPercW(1)).padLeft(fromCMToPercW(1)).uniformX();
        rootTable.add(itemSelectTable).align(Align.bottom).padBottom(20);

    }
}
