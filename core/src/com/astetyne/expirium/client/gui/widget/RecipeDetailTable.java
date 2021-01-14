package com.astetyne.expirium.client.gui.widget;

import com.astetyne.expirium.client.ExpiGame;
import com.astetyne.expirium.client.Res;
import com.astetyne.expirium.client.gui.roots.GameRoot;
import com.astetyne.expirium.client.items.ItemRecipe;
import com.astetyne.expirium.client.items.ItemStack;
import com.astetyne.expirium.client.screens.GameScreen;
import com.astetyne.expirium.client.utils.Utils;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class RecipeDetailTable extends Table implements RecipeListTable.RecipeChangeListener {

    private final Table requiredItems;
    private ItemRecipe selectedRecipe;
    private final ScrollPane scrollRequiredItems;
    private final Image returnButton;

    public RecipeDetailTable() {

        requiredItems = new Table();
        scrollRequiredItems = new ScrollPane(requiredItems);
        scrollRequiredItems.setScrollingDisabled(true, false);

        returnButton = new Image(Res.CROSS_ICON);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameScreen.get().setRoot(new GameRoot());
            }
        });

        rebuild();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(Res.INV_DETAIL_BACK, getX(), getY(), getWidth(), getHeight());
        super.draw(batch, parentAlpha);
    }

    @Override
    public void onRecipeChange(ItemRecipe recipe) {
        selectedRecipe = recipe;
        rebuild();
    }

    private void rebuild() {

        clear();

        if(selectedRecipe == null) selectedRecipe = ItemRecipe.getRecipe(0);
        Image imgDetail = new Image(selectedRecipe.getProduct().getItem().getItemTexture());
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
        add(returnButton).width(Utils.percFromH(100)).height(100).align(Align.topRight);
        row();
        add(imgDetail).width(Utils.percFromH(100)).height(100).pad(50,100,50,100);
        row();
        add(makeButton).width(240).height(80).padBottom(30);
        row();

        requiredItems.clear();

        for(ItemStack is : selectedRecipe.getRequiredItems()) {
            requiredItems.add(new Label(is.getAmount() +" "+is.getItem().getLabel(), Res.LABEL_STYLE));
            requiredItems.row();
        }
        add(scrollRequiredItems).expandX().height(200);
        row();
        add(desc).grow().width(360).pad(50, 20, 10, 20);

    }
}
