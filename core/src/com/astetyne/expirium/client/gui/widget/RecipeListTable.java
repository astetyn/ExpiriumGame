package com.astetyne.expirium.client.gui.widget;

import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.items.ItemRecipe;
import com.astetyne.expirium.client.resources.Res;
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
            Item item = recipe.getProduct().getItem();
            Image icon = new Image(item.getTexture());
            Label label = new Label(item.getLabel(), Res.LABEL_STYLE);
            label.setWrap(true);

            t.add(icon).width(80).height(Utils.percFromW(80)).align(Align.left).pad(10, 15, 10, 15);
            t.add(label).grow();
            t.setBackground(Res.FRAME_ROUND_GRAY);
            t.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(lastSelected != null) {
                        lastSelected.setBackground(Res.FRAME_ROUND_GRAY);
                    }
                    t.setBackground(Res.FRAME_ROUND_YELLOW);
                    listener.onRecipeChange(recipe);
                    lastSelected = t;
                }
            });

            // 340 is a magic value get from observations
            int height = Utils.getTextWidth(label.getText().toString(), label.getStyle().font) / 340 > 1 ? 140 : 112;
            add(t).width(450).height(height);
            row();
        }
    }

    public interface RecipeChangeListener {
        void onRecipeChange(ItemRecipe recipe);
    }
}
