package com.astetyne.expirium.client.gui.widget;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.Res;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.client.utils.Utils;
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
                ExpiGame.get().getClientGateway().getManager().putUIInteractPacket(slotType.getOnClick());
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        if(!focused) {
            batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
        }

        batch.draw(style.frame, getX(), getY(), getWidth(), getHeight());
        if(itemStack == null || itemStack.getItem() == Item.EMPTY) {
            Res.MAIN_FONT.setColor(0.2f, 0.2f, 0.2f, 1);
            float xOff = getWidth()/2 - Utils.getTextWidth(emptyLabel, Res.MAIN_FONT)/2;
            float yOff = getHeight()/2 + Utils.getTextHeight(emptyLabel, Res.MAIN_FONT)/2;
            Res.MAIN_FONT.draw(batch, emptyLabel, getX()+xOff, getY()+yOff);
            Res.MAIN_FONT.setColor(1,1,1,1);
        }else {
            batch.draw(itemStack.getItem().getItemTexture(), getX()+getWidth()/8, getY()+getHeight()/8, getWidth()*(6f/8), getHeight()*(6f/8));
            String amount = itemStack.getAmount()+"";
            float xOff = getWidth()/2 - Utils.getTextWidth(amount, Res.MAIN_FONT)/2;
            float yOff = getHeight()/2 + Utils.getTextHeight(amount, Res.MAIN_FONT)/2;
            Res.MAIN_FONT.draw(batch, amount, getX()+xOff, getY()+yOff);
        }
        batch.setColor(1,1,1,1);
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

        final TextureRegion frame;

        public HotBarSlotStyle(TextureRegion frame) {
            this.frame = frame;
        }
    }

}
