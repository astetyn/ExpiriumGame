package com.astetyne.expirium.client.gui.widget;

import com.astetyne.expirium.client.Res;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.items.ItemRecipe;
import com.astetyne.expirium.client.resources.GuiRes;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.Utils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class RecipeListTable extends Table {

    private Table lastSelected;

    public RecipeListTable(RecipeChangeListener listener) {

        for(ItemRecipe recipe : ItemRecipe.values()) {
            Table t = new Table();
            if(Consts.DEBUG) t.debugAll();
            Item item = recipe.getProduct().getItem();
            Image icon = new Image(item.getTexture());
            Label label = new Label(item.getLabel(), Res.LABEL_STYLE);
            label.setWrap(true);

            t.add(icon).width(80).height(Utils.percFromW(80)).align(Align.left).pad(10, 5, 10, 15);
            t.add(label).grow();
            t.setBackground(GuiRes.FRAME_GRAY.getDrawable());
            t.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(lastSelected != null) {
                        lastSelected.setBackground(GuiRes.FRAME_GRAY.getDrawable());
                    }
                    t.setBackground(GuiRes.FRAME_YELLOW.getDrawable());
                    listener.onRecipeChange(recipe);
                    lastSelected = t;
                }
            });
            add(t).width(450);
            row();
        }
    }

    public interface RecipeChangeListener {
        void onRecipeChange(ItemRecipe recipe);
    }
}
