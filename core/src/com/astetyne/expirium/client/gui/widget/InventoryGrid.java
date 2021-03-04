package com.astetyne.expirium.client.gui.widget;

import com.astetyne.expirium.client.data.ExtraCell;
import com.astetyne.expirium.client.data.GridData;
import com.astetyne.expirium.client.items.GridItemStack;
import com.astetyne.expirium.client.resources.GuiRes;
import com.astetyne.expirium.client.resources.Res;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.IntVector2;
import com.astetyne.expirium.client.utils.Utils;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public class InventoryGrid extends Table {

    private final BaseGrid grid;
    private final Label weightLabel, infoLabel;
    private final Image weightImage;
    private Cell<BaseGrid> gridCell;
    private final GridData data;

    public InventoryGrid(int rows, int columns, GridData data, ExtraCell[] extraCells) {
        this.data = data;
        grid = new BaseGrid(rows, columns, data, extraCells);
        weightLabel = new Label("0.0/0.0", Res.LABEL_STYLE);
        weightLabel.setAlignment(Align.left);
        weightImage = new Image(GuiRes.INV_WEIGHT.getDrawable());
        infoLabel = new Label("", Res.LABEL_STYLE);
        infoLabel.setColor(1, 0.6f, 0.1f, 1);
        infoLabel.setAlignment(Align.center);
        rebuild();
    }

    public GridItemStack getItemAt(float x, float y) {
        return grid.getItemAt(x - gridCell.getActorX(), y - gridCell.getActorY());
    }

    public IntVector2 getGridPos(float x, float y) {
        return grid.getGridPos(x - gridCell.getActorX(), y - gridCell.getActorY());
    }

    public BaseGrid getGrid() {
        return grid;
    }

    public void rebuild() {
        int mlt = Consts.INV_TILE_WIDTH;
        clear();
        gridCell = add(grid).width(grid.getColumns() * mlt).height(Utils.percFromW(grid.getRows() * mlt));
        row();
        add(infoLabel).expandX().align(Align.center).padTop(10);
        row();
        Table t = new Table();
        t.add(weightImage).width(60).height(Utils.percFromW(60)).padTop(10);
        t.add(weightLabel).pad(10, 20, 0,0);
        add(t);
        setTouchable(Touchable.enabled);
        grid.setZIndex(100);
    }

    public void refreshLabels() {
        weightLabel.setText(data.totalWeight+"/"+data.maxWeight);
        infoLabel.setText(data.label);
    }
}
