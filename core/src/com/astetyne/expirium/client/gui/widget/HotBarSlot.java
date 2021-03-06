package com.astetyne.expirium.client.gui.widget;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.client.resources.Res;
import com.astetyne.expirium.client.utils.Utils;
import com.astetyne.expirium.server.core.world.inventory.ChosenSlot;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class HotBarSlot extends Widget {

    private ItemStack itemStack;
    private boolean focused;
    private final String emptyLabel;
    private final ChosenSlot slotType;

    public HotBarSlot(String emptyLabel, ChosenSlot slotType) {
        itemStack = new ItemStack(Item.EMPTY);
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

        Res.FRAME_ROUND_GRAY.draw(batch, getX(), getY(), getWidth(), getHeight());
        if(itemStack == null || itemStack.getItem() == Item.EMPTY) {
            Res.MAIN_FONT.setColor(0.2f, 0.2f, 0.2f, 1);
            float xOff = getWidth()/2 - Utils.getTextWidth(emptyLabel, Res.MAIN_FONT)/2;
            float yOff = getHeight()/2 + Utils.getTextHeight(emptyLabel, Res.MAIN_FONT)/2;
            Res.MAIN_FONT.draw(batch, emptyLabel, getX()+xOff, getY()+yOff);
            Res.MAIN_FONT.setColor(1,1,1,1);
        }else {
            batch.draw(itemStack.getItem().getTexture(), getX()+getWidth()/8, getY()+getHeight()/8, getWidth()*(6f/8), getHeight()*(6f/8));
            String amount = itemStack.getAmount()+"";
            float xOff = getWidth()/2 - Utils.getTextWidth(amount, Res.MAIN_FONT)/2;
            float yOff = getHeight()/2 + Utils.getTextHeight(amount, Res.MAIN_FONT)/2;
            if(itemStack.getItem().isMergeable()) {
                Res.MAIN_FONT.draw(batch, amount, getX() + xOff, getY() + yOff);
            }
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

}
