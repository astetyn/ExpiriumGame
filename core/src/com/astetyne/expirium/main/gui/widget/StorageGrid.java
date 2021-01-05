package com.astetyne.expirium.main.gui.widget;

import com.astetyne.expirium.main.ExpiGame;
import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.items.Item;
import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.utils.IntVector2;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import java.util.ArrayList;
import java.util.List;

public class StorageGrid extends Widget {

    private final StorageGridStyle style;
    private final List<ItemStack> items;
    private ItemStack selItem;
    private final Vector2 selItemVec;
    private int columns, rows;
    private float totalWeight, maxWeight;
    private final Label weightLabel;

    public StorageGrid(StorageGridStyle style) {
        this(0,0,style);
    }

    public StorageGrid(int rows, int columns, StorageGridStyle style) {

        this.rows = rows;
        this.columns = columns;
        this.style = style;
        totalWeight = 0;
        maxWeight = 0;

        weightLabel = new Label("0.0/0.0", Res.LABEL_STYLE);

        items = new ArrayList<>();

        selItemVec = new Vector2();

        addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                selItem = getItemAt((int)x, (int) y);
                if(selItem == null) return false;
                selItemVec.set(getX() + x, getY() + y);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(isInsideGrid((int)x, (int) y)) {
                    x /= (getWidth() / columns);
                    y /= (getHeight() / rows);
                }else {
                    x = -1;
                    y = -1;
                }
                IntVector2 pos2 = new IntVector2((int)x, (int)y);
                ExpiGame.get().getClientGateway().getManager().putInvItemMoveReqPacket(true, selItem.getGridPos(), true, pos2);
                selItem = null;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if(selItem != null) {
                    selItemVec.set(getX() + x, getY() + y);
                }
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        int tileSizeX = (int) (getWidth() / columns);
        int tileSizeY = (int) (getHeight() / rows);

        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                batch.draw(style.gridTile, getX()+j*tileSizeX, getY()+i*tileSizeY, tileSizeX, tileSizeY);
            }
        }
        for(ItemStack is : items) {
            IntVector2 pos = is.getGridPos();
            TextureRegion tex = is.getItem().getItemTextureInGrid();
            int w = is.getItem().getGridWidth();
            int h = is.getItem().getGridHeight();
            if(is == selItem) {
                batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
            }
            batch.draw(tex, getX() + pos.x * tileSizeX, getY() + pos.y * tileSizeY, w * tileSizeX, h * tileSizeY);
            batch.setColor(1,1,1,1);
        }
        if(selItem != null) {
            TextureRegion tex = selItem.getItem().getItemTexture();
            int w = selItem.getItem().getGridWidth();
            int h = selItem.getItem().getGridHeight();
            batch.draw(tex, selItemVec.x, selItemVec.y, w*tileSizeX*2, h*tileSizeY*2);
        }
    }

    public void feed(PacketInputStream in) {
        totalWeight = in.getFloat();
        maxWeight = in.getFloat();
        weightLabel.setText(totalWeight+"/"+maxWeight);
        items.clear();
        int itemsNumber = in.getInt();
        for(int i = 0; i < itemsNumber; i++) {
            int itemID = in.getInt();
            int amount = in.getInt();
            IntVector2 pos = in.getIntVector();
            items.add(new ItemStack(Item.getType(itemID), amount, pos));
        }
    }

    private ItemStack getItemAt(int x, int y) {
        x /= (getWidth() / columns);
        y /= (getHeight() / rows);
        for(ItemStack is : items) {
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

    private boolean isInsideGrid(int x, int y) {
        if(selItem == null) return false;

        x /= (getWidth() / columns);
        y /= (getHeight() / rows);

        int sw = selItem.getItem().getGridWidth();
        int sh = selItem.getItem().getGridHeight();

        return x >= 0 && x + sw <= columns && y >= 0 && y + sh <= rows;
    }

    public static class StorageGridStyle {

        final TextureRegion gridTile;

        public StorageGridStyle(TextureRegion gridTile) {
            this.gridTile = gridTile;
        }
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public float getTotalWeight() {
        return totalWeight;
    }

    public float getMaxWeight() {
        return maxWeight;
    }

    public Label getWeightLabel() {
        return weightLabel;
    }

    public void setGrid(int r, int c) {
        rows = r;
        columns = c;
    }
}
