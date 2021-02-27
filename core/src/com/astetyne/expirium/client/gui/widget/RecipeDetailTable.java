package com.astetyne.expirium.client.gui.widget;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.Res;
import com.astetyne.expirium.client.items.ItemRecipe;
import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.client.utils.Utils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class RecipeDetailTable extends Table implements RecipeListTable.RecipeChangeListener {

    private final Table requiredItems;
    private ItemRecipe selectedRecipe;
    private final ScrollPane scrollRequiredItems;

    public RecipeDetailTable() {

        requiredItems = new Table();
        ScrollPane.ScrollPaneStyle style = new ScrollPane.ScrollPaneStyle(Res.FRAME_ROUND_GRAY, null, null, null, Res.FRAME_SQUARE_GRAY);
        scrollRequiredItems = new ScrollPane(requiredItems, style);
        scrollRequiredItems.setScrollingDisabled(true, false);

        rebuild();
    }

    @Override
    public void onRecipeChange(ItemRecipe recipe) {
        selectedRecipe = recipe;
        rebuild();
    }

    private void rebuild() {

        clear();

        if(selectedRecipe == null) selectedRecipe = ItemRecipe.getRecipe(0);

        Label itemNameLabel = new Label(selectedRecipe.getProduct().getItem().getLabel(), Res.LABEL_STYLE);
        itemNameLabel.setAlignment(Align.center);

        String amount = "";
        if(selectedRecipe.getProduct().getAmount() != 1) amount = "x"+selectedRecipe.getProduct().getAmount();
        Label productAmountLabel = new Label(amount, Res.LABEL_STYLE);
        productAmountLabel.setAlignment(Align.left);
        Image imgDetail = new Image(selectedRecipe.getProduct().getItem().getTexture());

        TextButton makeButton = new TextButton("Make", Res.TEXT_BUTTON_STYLE);
        makeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ExpiGame.get().getClientGateway().getManager().putInvItemMakeReqPacket(selectedRecipe);
            }
        });

        Label desc = new Label(selectedRecipe.getDescription(), Res.LABEL_STYLE);
        desc.setWrap(true);
        desc.setAlignment(Align.topLeft);

        add(itemNameLabel).height(Utils.percFromW(100));
        row();
        Table t = new Table();
        t.add(imgDetail).width(Utils.percFromH(100)).height(100).pad(50,30,50,30);
        t.add(productAmountLabel).align(Align.left);
        add(t);
        row();
        add(makeButton).width(240).height(80).padBottom(30);
        row();

        requiredItems.clear();
        for(ItemStack is : selectedRecipe.getRequiredItems()) {
            Image i = new Image(is.getItem().getTexture());
            Label l = new Label(is.getAmount() +" "+is.getItem().getLabel(), Res.LABEL_STYLE);
            l.setColor(Color.ORANGE);
            l.setWrap(true);
            requiredItems.add(i).width(50).height(Utils.percFromW(50)).padRight(20).padLeft(30);
            requiredItems.add(l).width(310);
            requiredItems.row();
        }
        add(scrollRequiredItems).expandX().height(210);
        row();
        add(desc).grow().width(360).pad(10, 20, 10, 20);
        setBackground(Res.FRAME_ROUND_GRAY_TRANSP);
        scrollRequiredItems.setScrollbarsVisible(true);

    }
}
