package com.astetyne.expirium.client.gui.widget;

import com.astetyne.expirium.client.Res;
import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.items.ItemRecipe;
import com.astetyne.expirium.client.utils.Consts;
import com.astetyne.expirium.client.utils.Utils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class RecipeListTable extends Table {

    public RecipeListTable(RecipeChangeListener listener) {

        for(ItemRecipe recipe : ItemRecipe.values()) {
            Table t = new Table();
            if(Consts.DEBUG) t.debugAll();
            Item item = recipe.getProduct().getItem();
            Image icon = new Image(item.getItemTexture());
            Label label = new Label(item.getLabel(), Res.LABEL_STYLE);
            t.add(icon).width(50).height(Utils.percFromW(50)).align(Align.left).pad(10, 5, 10, 5);
            t.add(label).grow();
            t.setBackground(Res.RECIPE_BACK);
            t.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    listener.onRecipeChange(recipe);
                }
            });
            add(t).width(200);
            row();
        }
    }

    public interface RecipeChangeListener {
        void onRecipeChange(ItemRecipe recipe);
    }
}
