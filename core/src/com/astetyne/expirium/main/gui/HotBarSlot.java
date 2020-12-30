package com.astetyne.expirium.main.gui;

import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.utils.Utils;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.List;

public class HotBarSlot extends Widget {

    private ItemStack itemStack;
    private final HotBarSlotStyle style;
    private boolean focused;
    private int index;
    private final int itemCategory;
    private final String emptyLabel;

    public HotBarSlot(HotBarSlotStyle style, Runnable onFocus, int itemCategory, String emptyLabel) {
        itemStack = null;
        this.style = style;
        this.itemCategory = itemCategory;
        focused = false;
        this.emptyLabel = emptyLabel;
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onFocus.run();
                focused = true;
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        if(!focused) {
            batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
        }

        batch.draw(style.background, getX(), getY(), getWidth(), getHeight());
        if(itemStack == null) {
            Res.ARIAL_FONT.setColor(0.2f, 0.2f, 0.2f, 1);
            float xOff = getWidth()/2 - Utils.getTextWidth(emptyLabel, Res.ARIAL_FONT)/2;
            float yOff = getHeight()/2 + Utils.getTextHeight(emptyLabel, Res.ARIAL_FONT)/2;
            Res.ARIAL_FONT.draw(batch, emptyLabel, getX()+xOff, getY()+yOff);
            Res.ARIAL_FONT.setColor(1,1,1,1);
        }else {
            batch.draw(itemStack.getItem().getItemTexture(), getX(), getY(), getWidth(), getHeight());
            String amount = itemStack.getAmount()+"";
            float xOff = getWidth()/2 - Utils.getTextWidth(amount, Res.ARIAL_FONT)/2;
            float yOff = getHeight()/5 + Utils.getTextHeight(amount, Res.ARIAL_FONT)/2;
            Res.ARIAL_FONT.draw(batch, amount, getX()+xOff, getY()+yOff);
        }
        batch.draw(style.frame, getX(), getY(), getWidth(), getHeight());
        batch.setColor(1,1,1,1);
    }

    public void saveItemFeed(List<ItemStack> items) {
        int tempCount = 0;
        ItemStack lastItem = null;

        for(ItemStack is : items) {
            if(is.getItem().getCategory() == itemCategory) {
                if(index == tempCount) itemStack = is;
                tempCount++;
                lastItem = is;
            }
        }

        if(index >= tempCount) {
            index = Math.max(tempCount-1, 0);
            itemStack = lastItem;
        }
    }

    @Override
    public float getPrefWidth() {
        return 60;
    }

    @Override
    public float getPrefHeight() {
        return Utils.percFromW(60);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public void setFocus(boolean focused) {
        this.focused = focused;
    }

    public boolean isFocused() {
        return focused;
    }

    public void increaseIndex() {
        index++;
    }

    public void decreaseIndex() {
        index--;
    }

    public void setIndex(int val) {
        this.index = val;
    }

    public int getIndex() {
        return index;
    }

    public static class HotBarSlotStyle {

        final TextureRegion background, frame, defaultItemTexture;

        public HotBarSlotStyle(TextureRegion bg, TextureRegion frame, TextureRegion dit) {
            this.background = bg;
            this.frame = frame;
            this.defaultItemTexture = dit;
        }
    }
}
