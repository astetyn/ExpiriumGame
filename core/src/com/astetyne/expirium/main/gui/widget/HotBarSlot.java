package com.astetyne.expirium.main.gui.widget;

import com.astetyne.expirium.main.ExpiGame;
import com.astetyne.expirium.main.Res;
import com.astetyne.expirium.main.items.Item;
import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.utils.Utils;
import com.astetyne.expirium.server.api.world.inventory.ChosenSlot;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class HotBarSlot extends Widget {

    private ItemStack itemStack;
    private final HotBarSlotStyle style;
    private boolean focused;
    private final String emptyLabel;
    private final ChosenSlot slotType;

    public HotBarSlot(HotBarSlotStyle style, String emptyLabel, ChosenSlot slotType) {
        itemStack = new ItemStack(Item.EMPTY);
        this.style = style;
        this.emptyLabel = emptyLabel;
        this.slotType = slotType;
        focused = false;
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ExpiGame.get().getClientGateway().getManager().putInvInteractPacket(slotType.getOnClick());
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        if(!focused) {
            batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
        }

        batch.draw(style.background, getX(), getY(), getWidth(), getHeight());
        if(itemStack == null || itemStack.getItem() == Item.EMPTY) {
            Res.MAIN_FONT.setColor(0.2f, 0.2f, 0.2f, 1);
            float xOff = getWidth()/2 - Utils.getTextWidth(emptyLabel, Res.MAIN_FONT)/2;
            float yOff = getHeight()/2 + Utils.getTextHeight(emptyLabel, Res.MAIN_FONT)/2;
            Res.MAIN_FONT.draw(batch, emptyLabel, getX()+xOff, getY()+yOff);
            Res.MAIN_FONT.setColor(1,1,1,1);
        }else {
            batch.draw(itemStack.getItem().getItemTexture(), getX(), getY(), getWidth(), getHeight());
            String amount = itemStack.getAmount()+"";
            float xOff = getWidth()/2 - Utils.getTextWidth(amount, Res.MAIN_FONT)/2;
            float yOff = getHeight()/5 + Utils.getTextHeight(amount, Res.MAIN_FONT)/2;
            Res.MAIN_FONT.draw(batch, amount, getX()+xOff, getY()+yOff);
        }
        batch.draw(style.frame, getX(), getY(), getWidth(), getHeight());
        batch.setColor(1,1,1,1);
    }

    @Override
    public float getPrefWidth() {
        return 60;
    }

    @Override
    public float getPrefHeight() {
        return Utils.percFromW(60);
    }

    public void setItemStack(ItemStack is) {
        itemStack = is;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setFocus(boolean focused) {
        this.focused = focused;
    }

    public boolean isFocused() {
        return focused;
    }

    public ChosenSlot getSlotType() {
        return slotType;
    }

    public static class HotBarSlotStyle {

        final TextureRegion background, frame;

        public HotBarSlotStyle(TextureRegion bg, TextureRegion frame) {
            this.background = bg;
            this.frame = frame;
        }
    }

}