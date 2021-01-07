package com.astetyne.expirium.main.gui.widget;

import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.items.StorageGridData;
import com.astetyne.expirium.main.utils.IntVector2;
import com.astetyne.expirium.main.utils.Utils;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public class StorageGrid extends Table {

    private final BaseGrid grid;
    private final Label weightLabel, infoLabel;
    private final Image weightImage;
    private Cell<BaseGrid> gridCell;
    private final StorageGridData data;

    public StorageGrid(StorageGridData data, boolean withUtils) {
        this.data = data;
        grid = new BaseGrid(Res.BASE_GRID_STYLE, data, withUtils);
        weightLabel = new Label("0.0/0.0", Res.LABEL_STYLE);
        weightLabel.setAlignment(Align.left);
        weightImage = new Image(Res.INV_WEIGHT);
        infoLabel = new Label("", Res.LABEL_STYLE);
        infoLabel.setColor(1, 0.6f, 0.1f, 1);
        infoLabel.setAlignment(Align.center);
        rebuild();
    }

    public ItemStack getItemAt(float x, float y) {
        return grid.getItemAt(x - gridCell.getActorX(), y - gridCell.getActorY());
    }

    public IntVector2 getGridPos(float x, float y) {
        return grid.getGridPos(x - gridCell.getActorX(), y - gridCell.getActorY());
    }

    public BaseGrid getGrid() {
        return grid;
    }

    public void rebuild() {
        int mlt = 60;
        clear();
        gridCell = add(grid).width(data.columns * mlt).height(Utils.percFromW(data.rows * mlt)).colspan(2);
        row();
        add(infoLabel).expandX().align(Align.center).padTop(10).colspan(2);
        row();
        add(weightImage).width(30).height(Utils.percFromW(30)).align(Align.left).padTop(20);
        add(weightLabel).expandX().align(Align.left).pad(20, 20, 0,0);
        setTouchable(Touchable.enabled);
        grid.setZIndex(100);
    }

    public void onFeedUpdate() {
        weightLabel.setText(data.totalWeight+"/"+data.maxWeight);
        infoLabel.setText(data.label);
    }
}
