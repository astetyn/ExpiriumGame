package com.astetyne.expirium.main.gui;

import com.astetyne.expirium.main.Resources;
import com.astetyne.expirium.main.items.ItemRecipe;
import com.astetyne.expirium.main.items.ItemStack;
import com.astetyne.expirium.main.items.ItemType;
import com.astetyne.expirium.main.stages.GameStage;
import com.astetyne.expirium.main.utils.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

public class InvGUILayout extends GUILayout {

    private final Table rootTable, recipeList, recipeDetail;
    private final Image returnButton;
    private final ScrollPane scrollProductsList;
    private ItemRecipe selectedRecipe;

    public InvGUILayout() {

        returnButton = new Image(Resources.CROSS_TEXTURE);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameStage.get().setActiveGuiLayout(GameStage.get().getGameGuiLayout());
            }
        });

        rootTable = new Table();
        rootTable.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if(Constants.DEBUG) rootTable.setDebug(true);

        recipeList = new Table();
        recipeDetail = new Table();

        scrollProductsList = new ScrollPane(recipeList);
        scrollProductsList.setScrollingDisabled(true, false);

    }

    @Override
    public void update() {

    }

    @Override
    public Table getRootTable() {
        return rootTable;
    }

    @Override
    public void build(Stage stage) {
        stage.clear();
        stage.addActor(rootTable);

        rootTable.clear();

        recipeList.clear();
        for(ItemRecipe recipe : GameStage.get().getInv().getItemRecipes()) {
            Table t = new Table();
            ItemType item = recipe.getProduct().getItem();
            Image icon = new Image(item.getItemTexture());
            Label label = new Label("ahoj", Resources.LABEL_STYLE);
            label.setFontScale(0.2f);
            t.add(icon).pad(10, 5, 10, 5);
            t.add(label);
            t.setBackground(new TextureRegionDrawable(Resources.RECIPE_BACK));
            recipeList.add(t);
            recipeList.row();
        }

        recipeDetail.clear();
        if(selectedRecipe == null) selectedRecipe = GameStage.get().getInv().getItemRecipes().get(0);
        Image imgDetail = new Image(selectedRecipe.getProduct().getItem().getItemTexture());
        TextButton makeButton = new TextButton("Make", Resources.TEXT_BUTTON_STYLE);
        Label desc = new Label(selectedRecipe.getDescription(), Resources.LABEL_STYLE);
        desc.setFontScale(0.2f);
        desc.setWrap(true);
        recipeDetail.add(imgDetail);
        recipeDetail.row();
        recipeDetail.add(makeButton);
        recipeDetail.row();
        for(ItemStack is : selectedRecipe.getRequiredItems()) {

        }
        recipeDetail.row();
        recipeDetail.add(desc).width(20);

        rootTable.row().expandX();
        rootTable.add(GameStage.get().getInv().getInventoryGrid());
        rootTable.add(scrollProductsList);
        rootTable.add(recipeDetail);
        rootTable.add(returnButton).align(Align.topRight);
    }
}
