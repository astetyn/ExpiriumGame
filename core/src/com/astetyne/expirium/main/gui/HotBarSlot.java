package com.astetyne.expirium.main.gui;

import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.stages.GameStage;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class HotBarSlot extends Widget {

    private ItemStack itemStack;
    private final HotBarSlotStyle style;
    private boolean focused;

    public HotBarSlot(HotBarSlotStyle style, Runnable onFocus) {
        itemStack = null;
        this.style = style;
        focused = false;
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                onFocus.run();
                focused = true;
                return true;
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
            batch.draw(style.defaultItemTexture, getX(), getY(), getWidth(), getHeight());
        }else {
            batch.draw(itemStack.getItem().getTexture(), getX(), getY(), getWidth(), getHeight());
            //todo: napisat na to kvantitu (kolko toho este je)
        }
        batch.draw(style.frame, getX(), getY(), getWidth(), getHeight());
        batch.setColor(1,1,1,1);
    }

    @Override
    public float getPrefWidth() {
        return GameStage.toPixels(80);
    }

    @Override
    public float getPrefHeight() {
        return GameStage.toPixels(80);
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

    public static class HotBarSlotStyle {

        final TextureRegion background, frame, defaultItemTexture;

        public HotBarSlotStyle(TextureRegion bg, TextureRegion frame, TextureRegion dit) {
            this.background = bg;
            this.frame = frame;
            this.defaultItemTexture = dit;
        }
    }

}
