package com.astetyne.expirium.client.gui.widget;

import com.astetyne.expirium.client.data.ExtraCell;
import com.astetyne.expirium.client.data.GridData;
import com.astetyne.expirium.client.items.GridItemStack;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.resources.Res;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.IntVector2;
import com.astetyne.expirium.client.utils.Utils;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class BaseGrid extends Widget {

    private final int rows, columns;
    private final GridData data;
    private final ExtraCell[] extraCells;

    private GridItemStack selectedItem;
    private final Vector2 itemVec;
    private int tileWidth, tileHeight;

    public BaseGrid(int rows, int columns, GridData data, ExtraCell[] extraCells) {
        this.rows = rows;
        this.columns = columns;
        this.data = data;
        this.extraCells = extraCells;
        itemVec = new Vector2();
    }

    @Override
    public void layout() {
        super.layout();
        tileWidth = Consts.INV_TILE_WIDTH;
        tileHeight = (int) Utils.percFromW(Consts.INV_TILE_WIDTH);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        validate();
        for(int i = 0; i < rows; i++) {
            outer:
            for(int j = 0; j < columns; j++) {
                for(ExtraCell cell : extraCells) {
                    if(cell.pos.x == j && cell.pos.y == i) {
                        batch.draw(cell.tex.getTex(), getX() + j * tileWidth, getY() + i * tileHeight, tileWidth, tileHeight);
                        continue outer;
                    }
                }
                Res.INV_CELL.draw(batch, getX() + j * tileWidth, getY() + i * tileHeight, tileWidth, tileHeight);
            }
        }

        for(GridItemStack is : data.items) {
            IntVector2 pos = is.getGridPos();
            TextureRegion tex = is.getItem().getGridTexture();
            int w = is.getItem().getGridWidth();
            int h = is.getItem().getGridHeight();
            if(selectedItem != null) {
                IntVector2 siPos = selectedItem.getGridPos();
                if(pos.x == siPos.x && pos.y == siPos.y) batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
            }
            float off = tileWidth * 0.1f;
            batch.draw(tex, getX() + pos.x * tileWidth + off, getY() + pos.y * tileHeight + off, w * tileWidth - 2*off, h * tileHeight - 2*off);
            batch.setColor(1,1,1,1);

            if(!is.getItem().isMergeable()) continue;

            String label = is.getAmount()+"";
            float xOff = tileWidth/1.4f - Utils.getTextWidth(label, Res.MAIN_FONT)/2;
            float yOff = tileHeight/4f + Utils.getTextHeight(label, Res.MAIN_FONT)/2;
            Res.MAIN_FONT.draw(batch, label, getX() + pos.x * tileWidth + xOff, getY() + pos.y * tileHeight + yOff);
        }

        if(selectedItem != null) {
            int size = 160;
            TextureRegion tex = selectedItem.getItem().getTexture();
            batch.draw(tex, itemVec.x - size/2f, itemVec.y - Utils.percFromW(size)/2, size, Utils.percFromW(size));
        }
    }

    public GridItemStack getItemAt(float x, float y) {
        x /= (getWidth() / columns);
        y /= (getHeight() / rows);
        for(GridItemStack is : data.items) {
            Item item = is.getItem();
            IntVector2 pos = is.getGridPos();
            int w = item.getGridWidth();
            int h = item.getGridHeight();
            if(x >= pos.x && x < pos.x + w && y >= pos.y && y < pos.y + h) {
                return is;
            }
        }
        return null;
    }

    public IntVector2 getGridPos(float x, float y) {
        x /= (getWidth() / columns);
        y /= (getHeight() / rows);
        if(x < 0 || x >= columns || y < 0 || y >= rows) return new IntVector2(-1, -1);
        return new IntVector2((int)x, (int)y);
    }

    public GridItemStack getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(GridItemStack selectedItem) {
        this.selectedItem = selectedItem;
    }

    public GridData getData() {
        return data;
    }

    public void updateVec(float x, float y) {
        itemVec.set(x, y);
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }
}
