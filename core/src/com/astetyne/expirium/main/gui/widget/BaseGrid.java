package com.astetyne.expirium.main.gui.widget;

import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.items.Item;
import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.items.StorageGridData;
import com.astetyne.expirium.main.utils.IntVector2;
import com.astetyne.expirium.main.utils.Utils;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class BaseGrid extends Widget {

    private final BaseGridStyle style;
    private final StorageGridData data;
    private ItemStack selectedItem;
    private final Vector2 itemVec;
    private final boolean withUtils;

    public BaseGrid(BaseGridStyle style, StorageGridData data, boolean withUtils) {
        this.style = style;
        this.data = data;
        this.withUtils = withUtils;
        itemVec = new Vector2();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        int tileSizeX = (int) (getWidth() / data.columns);
        int tileSizeY = (int) (getHeight() / data.rows);

        for(int i = 0; i < data.rows; i++) {
            for(int j = 0; j < data.columns; j++) {
                if(withUtils && i == 0 && j == data.columns-2) {
                    batch.draw(style.splitTile, getX()+j*tileSizeX, getY(), tileSizeX, tileSizeY);
                    batch.draw(style.throwTile, getX()+(j+1)*tileSizeX, getY(), tileSizeX, tileSizeY);
                    j++;
                    continue;
                }
                batch.draw(style.gridTile, getX()+j*tileSizeX, getY()+i*tileSizeY, tileSizeX, tileSizeY);
            }
        }
        for(ItemStack is : data.items) {
            IntVector2 pos = is.getGridPos();
            TextureRegion tex = is.getItem().getItemTextureInGrid();
            int w = is.getItem().getGridWidth();
            int h = is.getItem().getGridHeight();
            if(is == selectedItem) {
                batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
            }
            batch.draw(tex, getX() + pos.x * tileSizeX, getY() + pos.y * tileSizeY, w * tileSizeX, h * tileSizeY);
            batch.setColor(1,1,1,1);
            String label = is.getAmount()+"";
            float xOff = tileSizeX/1.4f - Utils.getTextWidth(label, Res.MAIN_FONT)/2;
            float yOff = tileSizeY/4f + Utils.getTextHeight(label, Res.MAIN_FONT)/2;
            Res.MAIN_FONT.draw(batch, label, getX() + pos.x * tileSizeX + xOff, getY() + pos.y * tileSizeY + yOff);
        }

        if(selectedItem != null) {
            int size = 160;
            TextureRegion tex = selectedItem.getItem().getItemTexture();
            batch.draw(tex, itemVec.x - size/2f, itemVec.y - Utils.percFromW(size)/2, size, Utils.percFromW(size));
        }
    }

    public ItemStack getItemAt(float x, float y) {
        x /= (getWidth() / data.columns);
        y /= (getHeight() / data.rows);
        for(ItemStack is : data.items) {
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
        x /= (getWidth() / data.columns);
        y /= (getHeight() / data.rows);
        if(x < 0 || x >= data.columns || y < 0 || y >= data.rows) return new IntVector2(-1, -1);
        return new IntVector2((int)x, (int)y);
    }

    public static class BaseGridStyle {

        final TextureRegion gridTile, throwTile, splitTile;

        public BaseGridStyle(TextureRegion gridTile, TextureRegion throwTile, TextureRegion splitTile) {
            this.gridTile = gridTile;
            this.throwTile = throwTile;
            this.splitTile = splitTile;
        }
    }

    public ItemStack getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(ItemStack selectedItem) {
        this.selectedItem = selectedItem;
    }

    public StorageGridData getData() {
        return data;
    }

    public void updateVec(float x, float y) {
        itemVec.set(x, y);
    }
}
