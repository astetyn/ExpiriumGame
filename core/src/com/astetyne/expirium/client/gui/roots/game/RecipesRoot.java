package com.astetyne.expirium.client.gui.roots.game;

import com.astetyne.expirium.client.items.Item;
import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.client.resources.Res;
import com.astetyne.expirium.client.screens.GameScreen;
import com.astetyne.expirium.client.utils.Utils;
import com.astetyne.expirium.server.core.world.inventory.CookingRecipe;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class RecipesRoot extends WidgetGroup implements GameRootable {

    public RecipesRoot(GameScreen game) {

        Image returnButton = new Image(Res.CROSS_ICON);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setRoot(new SettingsRoot(game));
            }
        });

        returnButton.setBounds(1880, 890, 100, Utils.percFromW(100));
        addActor(returnButton);

        Table table = new Table();

        for(CookingRecipe recipe : CookingRecipe.values()) {
            Table t = new Table();
            Item product = recipe.getProduct().getItem();
            Image icon = new Image(product.getTexture());
            Label label = new Label("->", Res.LABEL_STYLE);

            // all recipes now have amount = 1, so this is redundant
            /*Label amt = new Label(recipe.getProduct().getAmount()+"x", Res.LABEL_STYLE);
            amt.setColor(Color.GOLD);
            t.add(amt);*/
            t.add(icon).width(80).height(Utils.percFromW(80)).align(Align.left).pad(10, 15, 10, 15);
            t.add(label).width(50);
            t.setBackground(Res.FRAME_ROUND_GRAY);

            int iconSize = 60;
            for(ItemStack req : recipe.getRequiredItems()) {
                t.add(new Label(req.getAmount()+"x", Res.LABEL_STYLE)).padLeft(20);
                t.add(new Image(req.getItem().getTexture())).width(iconSize).height(Utils.percFromW(iconSize));
            }

            table.add(t).width(600).height(100);
            table.row();
        }

        ScrollPane.ScrollPaneStyle style = new ScrollPane.ScrollPaneStyle(Res.FRAME_ROUND_GRAY_TRANSP, null, null, null, null);
        ScrollPane pane = new ScrollPane(table, style);
        pane.setScrollingDisabled(true, false);
        pane.setBounds(680, 20, 640, 980);
        addActor(pane);

    }

    @Override
    public Actor getActor() {
        return this;
    }

    @Override
    public boolean isDimmed() {
        return true;
    }

    @Override
    public void refresh() {

    }

    @Override
    public boolean canInteractWithWorld() {
        return false;
    }
}
