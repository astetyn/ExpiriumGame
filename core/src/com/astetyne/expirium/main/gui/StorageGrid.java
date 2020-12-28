package com.astetyne.expirium.main.gui;

import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.items.ItemType;
import com.astetyne.expirium.main.stages.GameStage;
import com.astetyne.expirium.main.utils.IntVector2;
import com.astetyne.expirium.server.backend.PacketInputStream;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import java.util.ArrayList;
import java.util.List;

public class StorageGrid extends Widget {

    private final StorageGridStyle style;
    private final List<ItemStack> items;
    private ItemStack selectedItem;
    private final Vector2 selItemVec;
    private int id, columns, rows;
    private final Runnable onInvUpdate;

    public StorageGrid(int columns, int rows, StorageGridStyle style, Runnable onInvUpdate) {
        this(-1, columns, rows, style, onInvUpdate);
    }

    public StorageGrid(int id, int columns, int rows, StorageGridStyle style, Runnable onInvUpdate) {

        this.id = id;
        this.columns = columns;
        this.rows = rows;
        this.style = style;
        this.onInvUpdate = onInvUpdate;

        items = new ArrayList<>();

        selItemVec = new Vector2();

        addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                selectedItem = getItemAt((int)x, (int) y);
                selItemVec.set(getX() + x, getY() + y);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                selectedItem = null;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if(selectedItem != null) {
                    selItemVec.set(getX() + x, getY() + y);
                }
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        int tileSize = (int) (getWidth() / columns);

        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                batch.draw(style.gridTile, getX()+j*tileSize, getY()+i*tileSize, tileSize, tileSize);
            }
        }
        for(ItemStack is : items) {
            IntVector2 pos = is.getGridPos();
            TextureRegion tex = is.getItem().getItemTextureInGrid();
            int w = is.getItem().getGridWidth();
            int h = is.getItem().getGridHeight();
            if(is == selectedItem) {
                batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
            }
            batch.draw(tex, getX() + pos.y * tileSize, getY() + (rows - pos.x - 1) * tileSize, w * tileSize, h * tileSize);
            batch.setColor(1,1,1,1);
        }
        if(selectedItem != null) {
            TextureRegion tex = selectedItem.getItem().getItemTexture();
            int w = selectedItem.getItem().getGridWidth();
            int h = selectedItem.getItem().getGridHeight();
            batch.draw(tex, selItemVec.x, selItemVec.y, w*tileSize*2, h*tileSize*2);
        }
    }

    public void onInvFeed(PacketInputStream in) {
        items.clear();
        int itemsNumber = in.getInt();
        for(int i = 0; i < itemsNumber; i++) {
            int itemID = in.getInt();
            int amount = in.getInt();
            IntVector2 pos = in.getIntVector();
            items.add(new ItemStack(ItemType.getType(itemID), amount, pos));
        }
        onInvUpdate.run();
    }

    public void onInvItemMove(PacketInputStream in) {

    }

    @Override
    public float getPrefWidth() {
        return GameStage.toPixels(30) * columns;
    }

    @Override
    public float getPrefHeight() {
        return GameStage.toPixels(30) * rows;
    }

    private ItemStack getItemAt(int x, int y) {
        x /= (getWidth() / columns);
        y /= (getHeight() / rows);
        for(ItemStack is : items) {
            ItemType item = is.getItem();
            IntVector2 pos = is.getGridPos();
            int w = item.getGridWidth();
            int h = item.getGridHeight();
            if(x >= pos.x && x < pos.x + w && y >= pos.y && y < pos.y + h) {
                return is;
            }
        }
        return null;
    }

    public static class StorageGridStyle {

        final TextureRegion gridTile;

        public StorageGridStyle(TextureRegion gridTile) {
            this.gridTile = gridTile;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        GameStage.get().getInv().getStorageGridIDs().remove(this.id);
        GameStage.get().getInv().getStorageGridIDs().put(id, this);
        this.id = id;
    }

    public List<ItemStack> getItems() {
        return items;
    }
}
